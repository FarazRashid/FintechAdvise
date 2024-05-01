from flask import Flask, jsonify
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

if __name__ == '__main__':
    app.run(debug=True)