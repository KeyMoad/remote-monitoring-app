#!/usr/bin/env python3
# coding: utf-8
from pydantic import BaseModel


class Token(BaseModel):
    access_token: str
    token_type: str
