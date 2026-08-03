all: gen_html_responsive_templates
gen_html_responsive_templates:
	mjml -r mjml/account_created.mjml -o ./account_created.html
	mjml -r mjml/item_shared.mjml -o ./item_shared.html
	mjml -r mjml/item_revoked.mjml -o ./item_revoked.html
	mjml -r mjml/container_revoked.mjml -o ./container_revoked.html
	mjml -r mjml/container_shared.mjml -o ./container_shared.html
	mjml -r mjml/password_reset.mjml -o ./password_reset.html
	mjml -r mjml/passphrase_reset.mjml -o ./passphrase_reset.html
