from sqlalchemy import create_engine, Column, Integer, String, ForeignKey
from sqlalchemy.orm import Session
from src.config import DATABASE_URL
from sqlalchemy.ext.declarative import declarative_base
import datetime


Base = declarative_base()


# Подключение к БД.
def connect_db():
    engine = create_engine(DATABASE_URL, connect_args={})
    session = Session(bind=engine.connect())
    return session


# Пользователь.
class User(Base):
    __tablename__ = 'users'
    id = Column(Integer, primary_key=True)
    phone_number = Column(String)
    first_name = Column(String)
    last_name = Column(String)
    password = Column(String)
    created_time = Column(String, default=datetime.datetime.now())

    def get_filtered_data(self):
        return {
            'id': self.id,
            'phone_number': self.phone_number,
            'first_name': self.first_name,
            'last_name': self.last_name,
            'password': self.password,
            'created_time': self.created_time
        }


# Токен авторизации.
class AuthToken(Base):
    __tablename__ = 'auth_tokens'
    id = Column(Integer, primary_key=True)
    token = Column(String)
    user_id = Column(Integer, ForeignKey('users.id'))
    created_time = Column(String, default=datetime.datetime.now())


# Медицинский препарат.
class Medicament(Base):
    __tablename__ = 'medicaments'
    id = Column(Integer, primary_key=True)
    name = Column(String)
    type = Column(String)
    barcode = Column(String)
    created_time = Column(String, default=datetime.datetime.now())

    def get_filtered_data(self):
        return {
            'id': self.id,
            'name': self.name,
            'type': self.type,
            'barcode': self.barcode,
            'created_time': self.created_time
        }

