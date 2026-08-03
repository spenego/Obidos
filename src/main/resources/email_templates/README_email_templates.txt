Update
======

Issue #543

We don't use static HTML templates from postmark anymore. Updating 
responsive HTML with inline CSS is a nightmare.  Now we generate 
responsive HTML files using mjml framework which makes life super simple.

That means it is possible to design the email templates in MJML with ease
now and can be added new fields etc very easily.

* Before generating HTML templates from mjml, install mjml first by running:

 npm install mjml -g

* Then type 
  cd src/main/resources/email_templates
  make

--

@Deprecated
===========
The inlined HTML templates are adapted from: 
    https://github.com/wildbit/postmark-templates

--
First cut: Feb-04-2018 

How it works
============

I am trying to fix some things and now I have no idea how everything works. 
So I am writing it down how it works.

* mjml file just contains mustache tags like {{hello}}, {{html_message}} etc. 

* We generate HTML from mjml, so HTML file contains nothing but those tags.
The actual contents for the tags are defined in JSON in database which gets
mapped to NotificatonTemplateJSONDTO.java. The JSON key has corresponding
variable defiend in the DTO. for example, the JSON that describes info when
an item is shared is shown below:

{
  "id": 417,
  "button_title": "View Shared Item",
  "button_trouble": "If you are having trouble with the button above, copy and paste the URL below into your web browser.",
  "contact": "Please contact support at xxx-xxx-xxxx if you have any questions.",
  "footer": "Example Inc., Copyright 2018, All Rights Reserved 42 Maple Ave Exton, PA 19341 USA",
  "hello": "Hi {{name}},",
  "product_name": "Spenego Obidos Digital Privacy Management and Sharing Software",
  "product_url": "https://spenego.com",
  "subject": "A secured Item is shared with you",
  "html_message": "{{owner_name}} has shared a secured item with you. Click the button below to view the item and the information about the the person who shared the item with you. The click will work only if you are logged into the system.  If you are not logged in to the application, you can copy and paste the URL shown below to your browser after you log in.",
  "text_message": "{{owner_name}} has shared a secured item with you.  Please copy and paste the URL below into your web browser. It will only work if you are already logged into the application. If you are not logged in to the application, you can copy and paste the URL shown below to your browser after you log in.",
  "action_url": "",
  "title": "Secure Items shared with you"
}

The button_title variable in DTO will have the content "View Shared Item". Similarly
the html_message variable in DTO will have the content "{{owner_name}} has shared ....".
Now {{owner_name}} has to be replaced programatically. It is done in EmailActionsimpl.java.

==========================================================================================
To make modifications, edit the corresponding json file. Then run 'make'.
If a new stanza is to be added, edit the mjml/<???>.mjml file.
Then edit the correspoding json file. And do 'make'.
