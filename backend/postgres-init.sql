SELECT 'CREATE DATABASE iam_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'iam_db')\gexec
