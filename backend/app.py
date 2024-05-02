from flask import Flask, request,jsonify
import mysql.connector
from DatabaseConnection import DatabaseConnection

app = Flask(__name__)


@app.route('/connect')
def connect_to_database():
    try:
        with DatabaseConnection() as conn:
            return jsonify({'message': 'Connected successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to connect to MySQL: {err}'})

@app.route('/create_table')
def create_table():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            cursor.execute("CREATE TABLE IF NOT EXISTS inventory2 (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), quantity INTEGER)")
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Table created successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to create table: {err}'})
    
    
# @app.route('/create_user_table', methods=['POST'])
def create_users_table():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id VARCHAR(100) PRIMARY KEY,
                    name VARCHAR(50),
                    email VARCHAR(50),
                    country VARCHAR(50),
                    password VARCHAR(256),
                    phone VARCHAR(15),
                    fcmToken VARCHAR(256) DEFAULT NULL,
                    profilePictureUrl VARCHAR(256) DEFAULT NULL
                )
            """)
            conn.commit()
            cursor.close()
    except mysql.connector.Error as err:
        print(f'Failed to create table: {err}')
        
# @app.route('/create_goals_table', methods=['POST'])        
def create_goals_table():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            cursor.execute("CREATE TABLE IF NOT EXISTS Goal (goalId varchar(50), name VARCHAR(50) NOT NULL, target varchar(50) NOT NULL, goalDate varchar(20) NOT NULL, currentAmount varchar(20) NOT NULL, goalAmount varchar(20) NOT NULL, goalType varchar(20) NOT NULL, goalPriority varchar(10) NOT NULL, userId varchar(256) NOT NULL, PRIMARY KEY (goalId))")
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Table created successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to create table: {err}'})

@app.route('/add_goal', methods=['POST'])
def add_goal():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            goal = request.get_json()
            query = "INSERT INTO Goal (goalId, name, target, goalDate, currentAmount, goalAmount, goalType, goalPriority, userId) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
            values = (goal['goalId'], goal['name'], goal['target'], goal['goalDate'], goal['currentAmount'], goal['goalAmount'], goal['goalType'], goal['goalPriority'], goal['userId'])
            cursor.execute(query, values)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Goal added successfully'}), 201
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        print(f'Request data: {request.data}')
        return jsonify({'error': f'Failed to add goal: {err}'}), 400

@app.route('/get_goals', methods=['GET'])
def get_goals():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            user = request.get_json()
            query = "SELECT * FROM Goal WHERE userId = %s"
            values = (user['userId'],)
            cursor.execute(query, values)
            results = cursor.fetchall()
            goals = []
            for result in results:
                columns = [column[0] for column in cursor.description]
                goal = dict(zip(columns, result))
                goals.append(goal)
            return jsonify(goals), 200
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        print(f'Request data: {request.data}')
        return jsonify({'error': f'Failed to get goals: {err}'}), 400
    
@app.route('/signup', methods=['POST'])
def signup():
    #create_users_table()
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            user = request.get_json()
            hashed_password = user['password']
            query = "INSERT INTO users (id, name, email, country, password, phone) VALUES (%s, %s, %s, %s, %s, %s)"
            values = (user['id'], user['name'], user['email'], user['country'], hashed_password, user['phone'])
            cursor.execute(query, values)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'User registered successfully'}), 201
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        print(f'Request data: {request.data}')
        return jsonify({'error': f'Failed to register user: {err}'}), 400
    

@app.route('/login', methods=['POST'])
def login():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            user = request.get_json()
            query = "SELECT * FROM users WHERE email = %s and password = %s"
            values = (user['email'], user['password'])
            cursor.execute(query, values)
            result = cursor.fetchone()
            if result:
                print(f'Successful login: {user["email"]}')
                columns = [column[0] for column in cursor.description]
                user = dict(zip(columns, result))
                return jsonify(user), 200
            print(f'Failed login attempt: {user["email"]}')
            print(f'Request data: {request.data}')
            print(f'Query: {query} with values: {values}')
            return jsonify({'error': 'Invalid email or password'}), 401
    except Exception as e:
        print(f'Error: {e}')
        return jsonify({'error': f'Failed to login user: {e}'}), 400

if __name__ == '__main__':
    app.run(debug=True)