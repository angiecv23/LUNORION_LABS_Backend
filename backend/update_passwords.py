import psycopg2

conn = psycopg2.connect(dbname='lunorion_db', user='postgres', password='1542', host='localhost')
cur = conn.cursor()

new_hash = '$2b$12$82lXcEY6cM4GblSVJxVM.eEOnfAdFh/Ty3UU/I5XW5laeJPpbsE1y'
cur.execute("UPDATE usuario SET password_hash = %s", (new_hash,))
conn.commit()

cur.execute('SELECT email, password_hash FROM usuario')
for row in cur.fetchall():
    print(f"{row[0]}: {row[1][:30]}...")

cur.close()
conn.close()
print("Passwords updated successfully!")
