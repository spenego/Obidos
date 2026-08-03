/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.server.servlets;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.DottedLineSeparator;
import com.spenego.Obidos.server.utils.Logger;
import com.spenego.Obidos.server.utils.LoggerFactory;
import com.spenego.Obidos.server.utils.ObidosFileUtils;
import com.spenego.Obidos.shared.dto.AuditReport;
import com.spenego.Obidos.shared.exceptions.ServerSideException;

// WARNING:
// we are using itext 2.1.7, the last version can be used with commercial
// software. itextpdf 5+ is AGPL licensed.
//
// Update: we use the opensource openpdf which is a fork of iText, which is
// maintained. The API is the same. I tried many other libraries like 
// Apache PBFBox, Asciidoctorj. Alsciidontorj is the most horrible software I've
// seen in a file. PDBBox is ok but it is very low level and not that fun to
// to use.
// Aug-03-2024
public class GenerateAuditReport
{
	private static final Logger logger = LoggerFactory.getLogger(GenerateAuditReport.class);

	////////////////////////////////////////////////////////////////////////////
	// We are using OpenPDF. https://github.com/LibrePDF/OpenPDF
	// It is an OpenSource fork of iText (which we used to
	// use in the past and now not maintained). OpenPDF is actively maintained.
	// 
	// Why?
	// Because:
	//    - It is an OpenSource fork of iText (which we used to use in past and 
	//     but not maintained anymore and have security issues). OpenPDF is 
	//     actively maintained. iText is a commercial software now.
	//     The API is fun to use and same as iText API.
	//   - asciidoctorj sucks, Did not see such bad software in a while. 
	//     Completely and utterly garbage. ruby gem asciidoctor is excellent.
	//   - Apache PDFBox is OK but very low level and not fun to use at all.
	//   - Every other good PDF generators are commercial
	////////////////////////////////////////////////////////////////////////////
	
	// Add cover page
	private static void addCoverPage(final Document document, final AuditReport ar) throws DocumentException, IOException
	{
		float logo_scale = 0.6f;
		// Add logo
		// The Logo  file is src/main/resources/images/SpenegoLogo.png
		// So, do not forget to add images/ or the file will not be found by
		// java in class path.
  		// The Logo  file is src/main/resources/images/SpenegoLogo.png
  		final String logoFileName = "/images/SpenegoLogo.png";
  		File logoFile = ObidosFileUtils.getFileFromClassPath(logoFileName);
  		if (logoFile != null)
  		{
  			logger.info(() -> "MMM logo image file path: " + logoFile.getAbsolutePath());
  		}
  		else
  		{
  			logger.info(() -> "MMM ERROR could not find logo image file: " + logoFileName);
  		}

		Image logo = Image.getInstance(logoFile.getAbsolutePath());
		logo.scalePercent(logo_scale * 100);
		logo.setAlignment(Element.ALIGN_CENTER);
		// Calculate the scaled dimensions
		float scaledWidth = logo.getWidth() * logo_scale;
		float scaledHeight = logo.getHeight() * logo_scale;

		// Set absolute position to center the logo
		float pageWidth = document.getPageSize().getWidth();
		float pageHeight = document.getPageSize().getHeight();
		float topMargin = 200; // Increased from 36 to 72 points (1 inch) from
								// top
		logo.setAbsolutePosition((pageWidth - scaledWidth) / 2, pageHeight - scaledHeight - topMargin);

		document.add(logo);

		// Add title, version, and date
		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
		Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

		Paragraph title = new Paragraph("Obidos Audit Report", titleFont);
		title.setAlignment(Element.ALIGN_CENTER);
		title.setSpacingBefore(200);
		document.add(title);

		Paragraph version = new Paragraph(ar.getVersion(), normalFont);
		version.setAlignment(Element.ALIGN_CENTER);
		document.add(version);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(ar.getDate());
		Paragraph date = new Paragraph(today, normalFont);
		date.setAlignment(Element.ALIGN_CENTER);
		document.add(date);
	}

	private static void addTableHeader(PdfPTable table)
	{
		String[] headers = {"Setting", "Details"};
		for (String header : headers)
		{
			PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			table.addCell(cell);
		}
    }

	public static void addUsageAndSettingDetails(final Document document,
			final AuditReport report) throws ServerSideException
	{
    	Paragraph heading = new Paragraph("Usage and Setup Details",
    			FontFactory.getFont(FontFactory.HELVETICA_BOLD,16));
    	heading.setAlignment(Element.ALIGN_LEFT);
    	document.add(heading);

        ArrayList<String[]> tableData = new ArrayList<String[]>();
    	
  		tableData.add(new String[] {"Obidos Instance",  report.getFqdn()});
  		tableData.add(new String[] {"Licensed To",  report.getCompanyName()});
  		tableData.add(new String[] {"Customer ID", report.getCustomerId()});
  		tableData.add(new String[] {"License Type", report.getLicenseType()});
  		String expirationDate = "Never";
  		final Long expEpoch = report.getLicenseExpirationEpoch();
  		if (expEpoch != null)
  		{
  			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  			expirationDate = sdf.format(new Date(expEpoch * 1000L));
  		}
  		tableData.add(new String[] {"License Expiration", expirationDate});
  		String numberOfUsers = "Unlimited";
  		if (report.getNumberOfUsersInLicense() > 0)
  		{
  			numberOfUsers = "" + report.getNumberOfUsersInLicense();
  		}
  		tableData.add(new String[] {"Number of Users Supported", numberOfUsers});
  		tableData.add(new String[] {"Number of Active Users", "" + report.getTotalUsers()});
  		tableData.add(new String[] {"Number of Locked Users", "" + report.getTotalLockedUsers()});
  		tableData.add(new String[] {"Number of Tompstoned users", "" + report.getTotalTombstonedUsers()});
  		tableData.add(new String[] {"Number of Users logged in last Month", "" + report.getAverageNumberOfDailyUsersOverLastMonth()});
  		tableData.add(new String[] {"Number of Admins", "" + report.getTotalAdmins()});
  		tableData.add(new String[] {"Data Store", "" + report.getDocumentStorageDirectory()});

		File file = new File(report.getDocumentStorageDirectory());
		double size = file.getUsableSpace() / (1024.0 * 1024.0 * 1024.0);
		String GB = String.format("%.2f", size) + " GB";
  		tableData.add(new String[] {"Space Avaible on Data Store", GB});

  		final String mySqlDir = "/var/lib/mysql";
  		String mySqlDirSize = "Unknown";
  		File fileMySqlDir = new File(mySqlDir);
  		if (fileMySqlDir.exists())
  		{
  			mySqlDirSize = "" + fileMySqlDir.getUsableSpace()/(1024.0 * 1024.0 * 1024.0) + " GB";

  		}
  		tableData.add(new String[] {"Database Directory", "/var/lib/mysql"});
  		tableData.add(new String[] {"Space Avilable for Database", mySqlDirSize});

  		// create table with setting and details
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Add table headers
        addTableHeader(table);
        
        // create table
  		for (String[] row : tableData)
  		{
        	PdfPCell cell1 = new PdfPCell(new Phrase(row[0], FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(cell1);
            table.addCell(new PdfPCell(new Phrase(row[1])));
  		}
  		document.add(table);
  		
	}

   /**
	* 
	* Use OpenPDF to generate PDF audit report
	* 
	* @param auditReport
	* @return ByteArrayOutputStream
	* <p>
	* @author spgdev@spenego.com - Aug 4, 2024
	*/
	public static ByteArrayOutputStream genPDFAuditReport(final AuditReport auditReport)
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try(final Document document = new Document();
				final PdfWriter writer = PdfWriter.getInstance(document, bout))
		{
			document.open();

			// create cover page
			addCoverPage(document, auditReport);
			
			// Start adding pages
			// page 1
			document.newPage();
			addUsageAndSettingDetails(document, auditReport);
			
			// addAnotherPage(document, auditorReport)
			// etc.

			// add page number
	        // Add page number
	        writer.setPageEvent(new PdfPageEventHelper()
	        {
	            public void onEndPage(PdfWriter writer, Document document) {
	                PdfContentByte cb = writer.getDirectContent();
	                cb.beginText();
	                try
					{
						cb.setFontAndSize(BaseFont.createFont(), 10);
					} catch (DocumentException e)
					{
						throw new ServerSideException("Could not generate page number: " + e.getMessage());
					} catch (IOException e)
					{
						throw new ServerSideException("Could not generate page number: " + e.getMessage());
					}
	                cb.showTextAligned(Element.ALIGN_CENTER, 
	                    String.valueOf(writer.getPageNumber()), 
	                    (document.right() - document.left()) / 2 + document.leftMargin(), 
	                    document.bottom() - 10, 0);
	                cb.endText();
	            }
	        });
			
			document.close();
			
		} catch(Exception e) {
			logger.info(()-> "Could not generate PDF report: " + e);
			throw new ServerSideException("Could not create Audit report: " + e);
		}
		return bout;
	}
	
	// @Deprecated
	public static ByteArrayOutputStream getPdf(AuditReport ar) throws ServerSideException {
		try (Document document = new Document(); ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
			List list = new List();
			list.setListSymbol("\u2022 ");
			list.add(new ListItem(" Number of users: " + ar.getTotalUsers()));
			list.add(new ListItem(" Number of admins: " + ar.getTotalAdmins()));
			list.add(new ListItem(" Number of users using Two-Factor Password reset: " + ar.getTotalUsersUsing2FAPasswordReset()));
			list.add(new ListItem(" Number of locked user: " + ar.getTotalLockedUsers()));
			list.add(new ListItem(" Number of users marked as deleted: " + ar.getTotalTombstonedUsers()));
			list.add(new ListItem(" Number of items: " + ar.getTotalItems()));
			list.add(new ListItem(" Number of items assignments: "+ ar.getTotalItemAssignments()));
			list.add(new ListItem(" Number of audit records: " + ar.getTotalAuditRecords()));
			list.add(new ListItem(" Number of active users in last 15 minutes: " + ar.getTotalActiveUsersInLastFifteenMinutes()));
			list.add(new ListItem(" Number of active users in last 1 hour: " + ar.getTotalActiveUsersInLastHour()));

			// PdfWriter.getInstance(document, bout);
			document.open();
			document.addTitle("Spengo Obidos Audit Report");
			document.addCreationDate();
			document.add(new Paragraph("Spengo Obidos Audit Report"));
			document.add(new Paragraph(new Date().toString()));
		    document.add(new Chunk(new DottedLineSeparator()));
		    document.add(list);
			document.close();

			return bout;
		} catch (IOException e) {
			logger.info(()-> "Could not create Audit report: " + e);
			throw new ServerSideException("Could not create Audit report: " + e);
		} catch (DocumentException e) {
			logger.info(()-> "Could not create Audit report: " + e);
			throw new ServerSideException("Could not create Audit report: " + e);
		}
	}

	public static ByteArrayOutputStream getHtml(AuditReport ar)
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
                try
                {
                    String html = "<html><head></head><body>";
                    html = html + "<p style=\"text-align:center;\"><img style=\"transform:scale(0.75);\" src=\"SpenegoLogo.png\" alt=\"spenego\" /></p>";
		    html = html + "<h2 style=\"text-align:center;\">Obidos Audit Report</h2>";
		    html = html + "<div style=\"text-align:center;font-size:1.5em;\">Obidos Instance: " + ar.getFqdn() + "</div><br/>";
//		    LocalDate d = LocalDate.now();
  		    Date d = new Date();
		    html = html + "<div style=\"text-align:center;font-size:1.5em;\">" + d.toString() + "</div><br/><hr/><br/>";
		    html = html + "<div style=\"font-size:1.25em;\">";
		    html = html + "Licensed To: " + ar.getCompanyName() + "<br/>";
		    html = html + "Customer Id: " + ar.getCustomerId() + "<br/>";
		    html = html + "License Type: " + ar.getLicenseType() + "<br/>";

		    String expirationDate;
                    if (ar.getLicenseExpirationEpoch() == null)
                    {
                       expirationDate = "Never";
                    }
                    else
                    {
		        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
		        expirationDate = formatter.format(new java.util.Date(ar.getLicenseExpirationEpoch()*1000));
                    }
		    html = html + "License Expiration: " + expirationDate + "<br/><br/>";

		    if ( ar.getTotalLicensedUsers() == 0 )
                    {
		        html = html + "Number of User Licenses: unlimited</br>";
                    }
                    else
                    {
		        html = html + "Number of User Licenses: " + ar.getTotalLicensedUsers() + "</br>";
                    }

		    html = html + "Number of Active Users: " + ar.getTotalUsers() + "</br>";
		    html = html + "Number of Locked Users: " + ar.getTotalLockedUsers() + "</br>";
		    html = html + "Number of Tombstoned Users: " + ar.getTotalTombstonedUsers() + "</br>";
		    html = html + "Number of Users who logged in last Month: " + ar.getAverageNumberOfDailyUsersOverLastMonth() + "</br>";
		    html = html+ "Number of Admins: " + ar.getTotalAdmins() + "</br><br/>";

		    html = html + "Data Storage: " + ar.getDocumentStorageDirectory() + "</br>";

                    File file = new File(ar.getDocumentStorageDirectory());
                    double size = file.getUsableSpace()/(1024.0 * 1024.0 * 1024.0);

		    html = html + "Space Available on Data Store: " + String.format("%.2f",size) + "Gb.<br/><br/>";

		    html = html + "Mariadb directory: /var/lib/mysql</br>";

                    File mysqlFile = new File("/var/lib/mysql");
                    double mysqlFree = mysqlFile.getUsableSpace()/(1024.0 * 1024.0 * 1024.0);

		    html = html + "Space Available for Mariadb: " + String.format("%.2f",mysqlFree) + "Gb.<br/><br/>";

                    html = html + "</div></body></html>";
                    byte[] array = html.getBytes();
        
                    // Writes data to the output stream
                    bout.write(array);
		} 
                catch (Exception e)
		{
			logger.info(()-> "Could not create audiot report: " + e);
			return null;
		}
		return bout;
	}

}
