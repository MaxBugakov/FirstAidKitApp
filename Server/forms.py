from pydantic import BaseModel


# Форма логина пользователя.
class UserLoginForm(BaseModel):
    phone_number: str
    password: str


# Форма регистрации пользователя.
class UserCreateForm(BaseModel):
    phone_number: str
    first_name: str
    last_name: str
    password: str


# Форма добавления медикамента.
class MedicamentAddForm(BaseModel):
    name: str
    type: str
    barcode: str

class MedicamentUpdateForm(BaseModel):
    name: str = None
    type: str = None

class StopTokenForm(BaseModel):
    token: str

class UserUpdateForm(BaseModel):
    first_name: str = None
    last_name: str = None
    password: str = None




