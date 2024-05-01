from config import host, username, password, db_name, ssl_ca
import mysql.connector

class DatabaseConnection:
    def __enter__(self):
        self.conn = mysql.connector.connect(
            host=host,
            user=username,
            password=password,
            database=db_name,
            ssl_ca=ssl_ca
        )
        return self.conn

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.conn.close()