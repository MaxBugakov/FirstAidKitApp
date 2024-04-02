from fastapi import Depends, HTTPException
from starlette import status
from src.models import AuthToken, connect_db, Medicament


# Проверка токена авторизации.
def check_auth_token(token: str, database=Depends(connect_db)):
    if (token.strip() == "0"):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Неверный или отсутствующий auth_token"
        )
    auth_token = database.query(AuthToken).filter(AuthToken.token == token).one_or_none()
    if auth_token:
        return auth_token

    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail='Auth is failed',
    )


# Поиск лекарства по штрихкоду (пользователь).
def check_medicine_barcode(barcode: str, database=Depends(connect_db)):
    finded_barcode = database.query(Medicament).filter(Medicament.barcode == barcode).one_or_none()
    if finded_barcode:
        return finded_barcode

    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail='Лекарство не найдено',
    )
