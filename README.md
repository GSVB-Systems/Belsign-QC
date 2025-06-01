# Belsign - Quality Control

## Problem Statement
Current Issues:
- No centralized system for photo documentation
- Manual process can cause delays and errors
- No clear workflow for QA approvals
- Lack of role-based access control
- No automated reporting or email notification

## Need: A digital system that allows:
- Production workers to upload and save photos
- QA employees to review and approve documentation
- Admins to assign roles to users and oversee the process
- Efficient tracking, reporting, and communication

## Requirements:
- Take pictures of an expansion joint from different angles
- Associate pictures and responsible user to an order number
- Store the pictures
- Generate a final report
- Easy reporting to the customer
- User groups with different responsibilities
- Tablet-friendly

## Requirements and libraries:
In order to connect to the database, and login to automate Emails, use credentials found inside the associated report.
To run the program, clone the code and install the following libraries:
- OpenCV 4.0.9
- JavaMail API (Jakarta)
- Google Gmail API
- Itext


## Global Features:
All users of the system has access to these features - these are the bare minimum a user needs to function and interact with the application
- Login
- Order Selection
- Product Selection

## Admin:
The Admin in BelSign is able to manage new users, products and orders aswell as new tags to catagorize photos.
The admin has access to lists of:
- Orders
- Products
- Users
- Approval signature

## Operator:
Operators of the system has access to a camera, the function of the operators in the system is to add photo documentation to orders.
- Camera

## Quality Control:
The Quality controllers inside our system handles the approval of the products and orders, as well as generating a PDF for eventual claims.
- Photo approval
- Photo Comments
- Approve Order
- preview PDF
- Generate PDF
