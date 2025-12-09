import secrets, base64
key = secrets.token_bytes(32)
print(base64.urlsafe_b64encode(key).decode())
