RM=/bin/rm -f
PDFGENERATOR=pdflatex
ADMIN_PDF=admin_guide.pdf
USER_PDF=user_guide.pdf
ADMIN_CMD= $(PDFGENERATOR) admin_guide.tex >/dev/null 2>&1
USER_CMD= $(PDFGENERATOR) user_guide.tex >/dev/null 2>&1
all: guides

guides: admin_pdf user_pdf 

admin_pdf: 
	@echo "- Generate Admin Guide"
	@echo ""
	$(RM) $(ADMIN_PDF)
	$(ADMIN_CMD)
	$(ADMIN_CMD)
	/bin/ls -lt $(ADMIN_PDF)

user_pdf:
	@echo "- Generate User Guide"
	${RM} $(USER_PDF)
	$(USER_CMD)
	$(USER_CMD)
	/bin/ls -lt $(USER_PDF)
	@echo ""

clean:
	${RM} *.log *.gz *.out *.toc *.fls *.fdb_latexmk *.aux
	find . -name *.aux -exec ${RM} '{}' \;
