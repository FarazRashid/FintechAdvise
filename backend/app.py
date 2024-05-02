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
            cursor.execute("CREATE TABLE IF NOT EXISTS inventory7(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), quantity INTEGER)")
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Table created successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to create table: {err}'})


    
@app.route('/create_user_table', methods=['POST'])
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
                    profilePictureUrl VARCHAR(256) DEFAULT NULL,
                    age VARCHAR(20),
                    income VARCHAR(20),
                    occupation VARCHAR(20),
                    riskTolerance VARCHAR(20)
                )
            """)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Users table created successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to create users table: {err}'})
    

@app.route('/signup', methods=['POST'])
def signup():
    #create_users_table()
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            user = request.get_json()
            hashed_password = user['password']
            query = """
                INSERT INTO users 
                (id, name, email, country, password, phone,fcmToken,profilePictureUrl, age, income, occupation, riskTolerance) 
                VALUES 
                (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s,%s)
            """
            values = (
                user.get('id'), 
                user.get('name'), 
                user.get('email'), 
                user.get('country'), 
                hashed_password, 
                user.get('phone'), 
                None,
                None,
                user.get('age'), 
                user.get('income'), 
                user.get('occupation'), 
                user.get('riskTolerance')
            )
            # log the query and values for debugging
            print(f'Query: {query} with values: {values}')
            cursor.execute(query, values)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'User registered successfully'}), 201
    except mysql.connector.Error as err:
        # log the error and request data for debugging
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
    


@app.route('/users', methods=['GET'])
def get_users():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT * FROM users1")
            print(cursor.description)
            #print data
            print(cursor.fetchall())
            users = cursor.fetchall()   
            cursor.close()
            return jsonify(users), 200
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to get users: {err}'}), 400
    

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)