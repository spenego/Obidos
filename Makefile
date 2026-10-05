########################################################################
# Makefile for Spenego Obidos from Spenego Software LLC
# Requires https://github.com/muquit/markdown-toc-go
# Jul-05-2026 
########################################################################

.PHONY: all docs doc changelog

docs:
	chmod 644 ./README.md
	markdown-toc-go -i docs/README.md \
		-o ./README.md --glossary docs/glossary.txt -f
	chmod 444 ./README.md
changelog:
	markdown-toc-go -i docs/ChangeLog.md \
		-o ./ChangeLog.md --glossary docs/glossary.txt \
		-f -no-credit

doc: docs
