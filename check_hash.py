import bcrypt
hash_val = b'$2a$12$cgXNPOxFe6RL/NoiAHuLs.5vIGbE6wD2bZXPa8Pcvz7q6DzFx1nIK'
password = b'Admin@123'
print("Matches?", bcrypt.checkpw(password, hash_val))
