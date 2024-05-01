from config import host, username, password, db_name, ssl_ca

from flask import Flask, jsonify
import mysql.connector

app = Flask(__name__)


@app.route('/connect')
def connect_to_database():
    try:
        conn = mysql.connector.connect(
            host=host,
            user=username,
            password=password,
            database=db_name,
            ssl_ca=ssl_ca
        )
        return jsonify({'message': 'Connected successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to connect to MySQL: {err}'})

@app.route('/create_table')
def create_table():
    try:
        conn = mysql.connector.connect(
            host=host,
            user=username,
            password=password,
            database=db_name,
            ssl_ca=ssl_ca
        )
        cursor = conn.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS inventory (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), quantity INTEGER)")
        conn.commit()
        cursor.close()
        conn.close()
        return jsonify({'message': 'Table created successfully'})
    except mysql.connector.Error as err:
        return jsonify({'error': f'Failed to create table: {err}'})

if __name__ == '__main__':
    app.run(debug=True)
