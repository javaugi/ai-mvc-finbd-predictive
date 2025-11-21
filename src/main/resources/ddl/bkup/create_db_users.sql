CREATE USER finbda_dev_user WITH PASSWORD 'devpass';
CREATE USER finbda_test_user WITH PASSWORD 'testpass';
CREATE USER finbda_prod_user WITH PASSWORD 'prodpass';

GRANT ALL PRIVILEGES ON DATABASE finbda_dev TO finbda_dev_user;
GRANT ALL PRIVILEGES ON DATABASE finbda_test TO finbda_test_user;
GRANT ALL PRIVILEGES ON DATABASE finbda_prod TO finbda_prod_user;


✅ METHOD 1 — Use pgAdmin GUI (no SQL needed)
1. Create a new user/role

Open pgAdmin

In the left tree, expand Servers → PostgreSQL → Login/Group Roles

Right-click Login/Group Roles

Click Create > Login/Group Role

Fill in:

General tab

Name: finbda_dev_user

Definition tab

Set password:

Password: devpass

Confirm: devpass

Privileges tab

Enable:

✔ Can login

(optionally) ✔ Create DB (not required)

Leave others unchecked

Click Save.

Repeat steps for:

finbda_test_user

2. Grant user access to each database

Expand Databases → FINBDA_DEV

Right-click FINBDA_DEV → Properties

Go to Privileges

Under Privileges → Add

Role: finbda_dev_user

Check:

✔ Connect

✔ Temporary

(If you want full control) check:

✔ Create

✔ Usage

✔ All

Click Save.

Repeat the process:

For FINBDA_TEST grant privileges to finbda_test_user

That’s it.

✅ METHOD 2 — Use Query Tool (SQL method)

This is fast and clean if you're comfortable with SQL.

Open Query Tool

Right-click on the server or database → Query Tool

Run this SQL for DEV:
CREATE USER finbda_dev_user WITH PASSWORD 'devpass';

GRANT ALL PRIVILEGES ON DATABASE FINBDA_DEV TO finbda_dev_user;

Run this SQL for TEST:
CREATE USER finbda_test_user WITH PASSWORD 'testpass';

GRANT ALL PRIVILEGES ON DATABASE FINBDA_TEST TO finbda_test_user;

Optional but recommended: Grant permissions on all tables

Inside each database (DEV and TEST):

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO finbda_dev_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO finbda_dev_user;


Same for TEST user.

🔥 Important — You must switch database in pgAdmin before running table grants

To run grants inside FINBDA_DEV:

In pgAdmin tree: right-click FINBDA_DEV → Query Tool

Then run your GRANT statements.

Repeat separately for FINBDA_TEST.

🚀 RESULT

You will have:

Database	Username	Privileges
FINBDA_DEV	finbda_dev_user	Full access
FINBDA_TEST	finbda_test_user	Full access
