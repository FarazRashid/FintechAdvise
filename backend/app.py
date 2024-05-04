from flask import Flask, request,jsonify, send_from_directory, url_for
import mysql.connector
from DatabaseConnection import DatabaseConnection
from werkzeug.utils import secure_filename
import time
import random   
import base64
import os


UPLOAD_FOLDER = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'images')
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

app = Flask(__name__,static_folder='images')

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER



@app.route('/connect')
def connect_to_database():
    try:
        with DatabaseConnection() as conn:
            return jsonify({'message': 'Connected successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to connect to MySQL: {err}'})

@app.route('/create_investment_table', methods=['POST'])
def create_investment_table():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS Investments (
                    id VARCHAR(100) PRIMARY KEY,
                    name VARCHAR(50),
                    investmentImageUrl VARCHAR(256) DEFAULT NULL,
                    type ENUM('STOCK', 'BOND', 'MUTUAL_FUND'),
                    currentValue DOUBLE,
                    performanceIndex DOUBLE
                )
            """)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Investment table created successfully'})
    except mysql.connector.Error as err:
        print(f'Failed to create table: {err}')

@app.route('/add_investment', methods=['POST'])
def add_investment():
    try:
        print(f'Request data: {request.data}')  # Print the request data
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            investment = request.get_json()
            # Save the image and get the filename
            filename = secure_filename(f'{time.time()}_{random.randint(0, 1000)}.png')
            save_image(investment['image'], filename)
            # Construct the image URL
            investmentImageUrl = url_for('static', filename=filename)
            # Prepare the SQL query
            query = """
                INSERT INTO Investments 
                (id, name, investmentImageUrl, type, currentValue, performanceIndex) 
                VALUES 
                (%s, %s, %s, %s, %s, %s)
            """
            values = (
                investment['id'], 
                investment['name'], 
                investmentImageUrl, 
                investment['type'], 
                investment['currentValue'], 
                investment['performanceIndex']
            )
            print(f'Query: {query}')  # Print the SQL query
            print(f'Values: {values}')  # Print the values
            cursor.execute(query, values)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'Investment added successfully'}), 201
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        return jsonify({'error': f'Failed to add investment: {err}'}), 400
    

@app.route('/get_investments', methods=['GET'])
def get_investments():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("""
                SELECT Investments.*, InvestmentPerformance.date, InvestmentPerformance.value 
                FROM Investments 
                LEFT JOIN InvestmentPerformance ON Investments.id = InvestmentPerformance.investment_id
            """)
            raw_investments = cursor.fetchall()
            cursor.close()

            # Process the raw investments to group by investment and collect dates and values
            investments = {}
            for inv in raw_investments:
                id = inv['id']
                if id not in investments:
                    # Create a new dictionary for the investment
                    investments[id] = {
                        'id': inv['id'],
                        'name': inv['name'],
                        'type': inv['type'],
                        'currentValue': inv['currentValue'],
                        'performanceIndex': inv['performanceIndex'],
                        'investmentImageUrl': inv['investmentImageUrl'],
                        'dates': [],
                        'values': [],
                    }
                investments[id]['dates'].append(inv['date'])
                investments[id]['values'].append(inv['value'])

            return jsonify(list(investments.values())), 200
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to get investments: {err}'}), 400
    



@app.route('/create_investment_performance_table', methods=['POST'])
def create_investment_performance_table():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS InvestmentPerformance (
                    date VARCHAR(20),
                    value DOUBLE,
                    investment_id VARCHAR(100),
                    FOREIGN KEY(investment_id) REFERENCES Investments(id)
                )
            """)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'InvestmentPerformance table created successfully'})
    except mysql.connector.Error as err:
        print(f'Failed to create table: {err}')

@app.route('/create_users_investments_table', methods=['POST'])
def create_users_investments_table():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS UsersInvestments (
                    user_id VARCHAR(100),
                    investment_id VARCHAR(100),
                    allocation DOUBLE,
                    PRIMARY KEY(user_id, investment_id),
                    FOREIGN KEY(user_id) REFERENCES Users(id),
                    FOREIGN KEY(investment_id) REFERENCES Investments(id)
                )
            """)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'UsersInvestments table created successfully'})
    except mysql.connector.Error as err:
        print(f'Failed to create table: {err}')

@app.route('/add_users_investments', methods=['POST'])
def add_users_investments():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor()
            user_investment = request.get_json()
            query = "INSERT INTO UsersInvestments (user_id, investment_id, allocation) VALUES (%s, %s, %s)"
            values = (user_investment['user_id'], user_investment['investment_id'], user_investment['allocation'])
            cursor.execute(query, values)
            conn.commit()
            cursor.close()
            return jsonify({'message': 'User investment added successfully'}), 201
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        print(f'Request data: {request.data}')
        return jsonify({'error': f'Failed to add user investment: {err}'}), 400
    
@app.route('/get_users_investments', methods=['POST'])
def get_users_investments():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor(dictionary=True)
            user = request.get_json()
            print(f'Received user_id: {user["user_id"]}')  # Debugging line
            query = "SELECT investment_id, allocation FROM UsersInvestments WHERE user_id = %s"
            values = (user['user_id'],)
            print(f'Executing query: {query % values}')  # Debugging line
            cursor.execute(query, values)
            results = cursor.fetchall()
            investment_ids = [row['investment_id'] for row in results]
            allocations = [row['allocation'] for row in results]
            cursor.close()
            return jsonify({'investment_ids': investment_ids, 'allocations': allocations}), 200
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        print(f'Request data: {request.data}')
        return jsonify({'error': f'Failed to get user investments: {err}'}), 400

@app.route('/get_investment_performance', methods=['POST'])
def get_investment_performance():
    try:
        with DatabaseConnection() as conn:
            cursor = conn.cursor(dictionary=True)
            investment = request.get_json()
            if isinstance(investment, list):
                investment = investment[0]  # or some other index
            investment_id = investment['investment_id']
            query = "SELECT * FROM InvestmentPerformance WHERE investment_id = %s"
            values = (investment_id,)
            print(f'Query: {query} with values: {values}')
            cursor.execute(query, values)
            performances = cursor.fetchall()
            print(f'Performances: {performances}')
            cursor.close()
            return jsonify(performances), 200
    except mysql.connector.Error as err:
        print(f'Error: {err}')
        print(f'Request data: {request.data}')
        print(f'Query: {query} with values: {values}')
        return jsonify({'error': f'Failed to get investment performances: {err}'}), 400


        
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
    
@app.route('/get_goals', methods=['POST'])
def get_goals():
        try:
           with DatabaseConnection() as conn:
                    cursor = conn.cursor()
                    user = request.get_json()
                    query = "SELECT * FROM Goal WHERE userId = %s"
                    values = (user[0]['userId'],)
                    cursor.execute(query, values)
                    results = cursor.fetchall()
                    goals = []
                    for result in results:
                        columns = [column[0] for column in cursor.description]
                        goal = dict(zip(columns, result))
                        goals.append(goal)
                    print(f'Goals: {goals}')
                    return goals, 200
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
    

def save_image(image_base64, filename):
    # decode the base64 string
    image_data = base64.b64decode(image_base64)
    # create a file object and save it using time and random number combination
    file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
    with open(file_path, 'wb') as f:
        f.write(image_data)
   
    

@app.route('/upload_image', methods=['POST'])
def upload_image():
    # check if the post request has the image part
    if 'image' not in request.form:
        return jsonify({'error': 'No image part in the request'}), 400
    image_base64 = request.form['image']
    filename = secure_filename(f'{time.time()}_{random.randint(0, 1000)}.png')
    save_image(image_base64, filename)
    return jsonify({'message': 'Image uploaded successfully'}), 200
    
def get_image(filename):
    try:
        # Check if file exists
        if os.path.isfile(os.path.join(app.config['UPLOAD_FOLDER'], filename)):
            print(url_for('static', filename=filename))
            return jsonify({'url': url_for('static', filename=filename)}), 200
        else:
            return jsonify({'error': 'Image not found'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)