use obidos;
update system_config set server_port = 443, fqdn = "localhost", two_factor_auth_issuer = "2fa.localhost" where id=1;

-- Set admin password to "admin" with forced change on first login.
-- The app will prompt the user to set a compliant password after logging in.
update users set
    password_digest = '$argon2id$v=19$m=65536,t=2,p=1$sc895QoKb3TBbUJnRfMoRA$wA3wX0x3Q2pJGchwPkD6WbVkNc3Enf+mM4f7uoVT+9E',
    salt = NULL,
    password_change_required = 1
where username = 'admin';

-- max_age_in_days=0 is treated as "expire immediately"; set to 100 years so
-- evaluators are not forced to change password on first login.
update complexity_requirements set max_age_in_days = 36500 where name = 'default';
