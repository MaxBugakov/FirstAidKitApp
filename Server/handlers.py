from fastapi import APIRouter, Body, Depends, HTTPException, Header
from src.forms import UserLoginForm, UserCreateForm, MedicamentAddForm, MedicamentUpdateForm, StopTokenForm, UserUpdateForm
from src.models import connect_db, User, AuthToken, Medicament
from src.utils import get_password_hash
import uuid
from starlette import status
from src.config import ADMIN_TOKEN
import datetime


router = APIRouter()


# Логин пользователя.
@router.post("/login", name="user:login")
def login(user_form: UserLoginForm = Body(..., embed=True), database=Depends(connect_db)):
    user = database.query(User).filter(User.phone_number == user_form.phone_number).one_or_none()
    if not user or get_password_hash(user_form.password) != user.password:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Номер или пароль неправильные"
        )

    auth_token = database.query(AuthToken).filter(AuthToken.user_id == user.id).one_or_none()
    if not auth_token:
        auth_token = AuthToken(token=str(uuid.uuid4()), user_id=user.id, created_time=str(datetime.datetime.now()))
        database.add(auth_token)
        database.commit()
    else:
        auth_token.token = str(uuid.uuid4())
        database.commit()
    return {'auth_token': auth_token.token}


# Регистрация пользователя.
@router.post('/user', name='user:create')
def create_user(user: UserCreateForm = Body(..., embed=True), database=Depends(connect_db)):
    exists_user = database.query(User).filter(User.phone_number == user.phone_number).one_or_none()
    if exists_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail='Пользователь с таким номером уже зарегистрирован'
        )

    new_user = User(
        phone_number=user.phone_number,
        first_name=user.first_name,
        last_name=user.last_name,
        password=get_password_hash(user.password),
        created_time=str(datetime.datetime.now())
    )
    database.add(new_user)
    database.commit()
    return {'new_user_id': new_user.id}


# Получение пользователя по токену аунтефикации.
@router.get('/user', name='user:get')
def get_user(token: str, database=Depends(connect_db)):
    if (token.strip() == "0"):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    auth_token = database.query(AuthToken).filter(AuthToken.token == token).one_or_none()
    if not auth_token:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    user = database.query(User).filter(User.id == auth_token.user_id).one_or_none()
    return {'user': user.get_filtered_data()}


# Изменение данных о пользователе.
@router.put('/user', name='user:update')
def update_medicament(token: str, user_data: UserUpdateForm = Body(..., embed=True), database=Depends(connect_db)):
    if (token.strip() == "0"):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    auth_token = database.query(AuthToken).filter(AuthToken.token == token).one_or_none()
    if not auth_token:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    user = database.query(User).filter(User.id == auth_token.user_id).one_or_none()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Пользователя с таким токеном не существует"
        )

    if user_data.first_name is not None:
        user.first_name = user_data.first_name
    if user_data.last_name is not None:
        user.last_name = user_data.last_name
    if user_data.password is not None:
        user.password = get_password_hash(user_data.password)

    database.commit()
    return {"message": "Данные пользователя обновлены"}


# Удаление пользователя.
@router.delete('/user', name='user:delete')
def get_user(token: str, database=Depends(connect_db)):
    if (token.strip() == "0"):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    auth_token = database.query(AuthToken).filter(AuthToken.token == token).one_or_none()
    if not auth_token:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    user = database.query(User).filter(User.id == auth_token.user_id).one_or_none()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Пользователя с таким токеном не существует"
        )

    # Удаление лекарства.
    database.delete(auth_token)
    database.delete(user)
    database.commit()
    return {"message": "Пользователь удалён"}


# Прекращение работы токена.
@router.post('/stop-token', name='stop-token:post')
def stop_token(auth_token_form: StopTokenForm = Body(..., embed=True), database=Depends(connect_db)):
    if (auth_token_form.token.strip() == "0"):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail='Неверный токен авторизации'
        )
    auth_token = database.query(AuthToken).filter(AuthToken.token == auth_token_form.token).one_or_none()
    if not auth_token:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Неверный токен авторизации"
        )
    user = database.query(User).filter(User.id == auth_token.user_id).one_or_none()
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Пользователя с таким токеном авторизации не существует"
        )

    auth_token.token = "0"
    database.commit()
    return {"message": "Токен авторизации снят"}


# Создание лекарства (админ).
@router.post('/medicament', name='medicament:create')
def create_medicament(medicament: MedicamentAddForm = Body(..., embed=True), admin_token: str = Header(...), database=Depends(connect_db)):
    # Проверка токена админа.
    if (admin_token != ADMIN_TOKEN):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Неверный или отсутствующий admin_token"
        )
    # Проверка на существование лекарства.
    exists_medicament = database.query(Medicament).filter(Medicament.barcode == medicament.barcode).one_or_none()
    if exists_medicament:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail='Такое лекарство уже существует'
        )
    # Создание нового лекарства.
    new_medicament = Medicament(
        name=medicament.name,
        type=medicament.type,
        barcode=medicament.barcode,
        created_time=str(datetime.datetime.now())
    )
    database.add(new_medicament)
    database.commit()
    return {'new_medicament_id': new_medicament.id}


# Обновление лекарства (админ).
@router.put('/medicament', name='medicament:update')
def update_medicament(id: int, medicament_data: MedicamentUpdateForm = Body(..., embed=True), admin_token: str = Header(...), database=Depends(connect_db)):
    # Проверка токена админа.
    if admin_token != ADMIN_TOKEN:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Неверный или отсутствующий admin_token"
        )

    # Поиск лекарства по id.
    medicament_to_update = database.query(Medicament).filter(Medicament.id == id).first()
    if not medicament_to_update:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Лекарство не найдено"
        )

    if medicament_data.name is not None:
        medicament_to_update.name = medicament_data.name
    if medicament_data.type is not None:
        medicament_to_update.type = medicament_data.type

    database.commit()
    return {"message": "Данные лекарства обновлены"}


# Удаление лекарства (админ).
@router.delete('/medicament', name='medicament:delete')
def delete_medicament(id: int, admin_token: str = Header(...), database=Depends(connect_db)):
    # Проверка токена админа.
    if admin_token != ADMIN_TOKEN:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Неверный или отсутствующий admin_token"
        )

    # Поиск лекарства по id.
    medicament_to_delete = database.query(Medicament).filter(Medicament.id == id).first()
    if not medicament_to_delete:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Лекарство не найдено"
        )

    # Удаление лекарства.
    database.delete(medicament_to_delete)
    database.commit()
    return {"message": "Лекарство удалено"}


# Получение лекарства (админ).
@router.get('/medicament', name='medicament:get')
def get_medicament(id: int, admin_token: str = Header(...), database=Depends(connect_db)):
    # Проверка токена админа.
    if admin_token != ADMIN_TOKEN:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Неверный или отсутствующий admin_token"
        )

    # Поиск лекарства по id.
    medicament_to_find = database.query(Medicament).filter(Medicament.id == id).first()
    if not medicament_to_find:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Лекарство не найдено"
        )

    return {'medicament': medicament_to_find.get_filtered_data()}


# Получение лекарства по штрихкоду.
@router.get('/get-medicament', name='get-medicament:get')
def get_medicament(barcode: str, database=Depends(connect_db)):
    finded_medicament = database.query(Medicament).filter(Medicament.barcode == barcode).one_or_none()
    if not finded_medicament:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail='Лекарство не найдено'
        )

    return {'medicament': finded_medicament.get_filtered_data()}
