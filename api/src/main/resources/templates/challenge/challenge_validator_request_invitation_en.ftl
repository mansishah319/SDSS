<!DOCTYPE html>
<html lang="en" dir="ltr">

<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>New Challenge Validation Request</title>
    <style>
        body {
            position: relative;
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
            text-align: center;
        }

        header {
            background-color: green;
            color: white;
            padding: 40px;
            z-index: 1;
            text-align: center;
        }

        .container {
            max-width: 100%;
            margin: 20px auto;
            padding: 20px;
            background-color: #ffffff;
            border: 1px solid #ddd;
            border-radius: 5px;
            direction: ltr;
            position: relative;
            text-align: center;
        }

        .table-container {
            margin-left: 20px;
        }

        .table-content-container {
            display: table;
            margin: 0 auto;
            width: 100%;
        }

        .table-row {
            display: table-row;
        }

        .table-cell-label,
        .table-cell-value {
            display: table-cell;
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        .table-cell-label {
            font-weight: bold;
            color: #000000;
        }

        .accept-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: green;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
            width: 80%;
        }

        footer {
            background-color: black;
            color: white;
            padding-top: 40px;
            padding-bottom: 40px;
            width: 100%;
            text-align: center;
        }
    </style>
</head>

<body>
<table width="100%">
    <tr>
        <td style="width: 15%; background-color: #ff0000; border-radius: 5px 0 0 5px; height: 100%;"></td>
        <td>
            <header>
                <img src="cid:logo.ico" alt="logo" style="max-width: 50%"/>
            </header>
            <div class="container">
                <div class="table-container">
                    <h2>Challenge Validation Request</h2>
                    <p>Hello ${user.fullName},</p>
                    <p>You have a request for the validation of the following challenge:</p>
                    <h3>${challenge.titleEn}</h3>
                    <div class="table-content-container">
                        <div class="table-row">
                            <div class="table-cell-label">Date:</div>
                            <div class="table-cell-value">${challenge.startTime}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-cell-label">Start Time:</div>
                            <div class="table-cell-value">${challenge.startTime}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-cell-label">End Time:</div>
                            <div class="table-cell-value">${challenge.endTime}</div>
                        </div>
                    </div>
                </div>
                <p>Additional information about the validation request goes here...</p>
                <p>Thank you for your quick attention and cooperation!</p>
                <a href="${ChallengeUserInvitationUrl}" class="accept-button">View Validation Request</a>
            </div>
            <footer>
                <p>&copy; 2023 Dubai Police. All rights reserved.</p>
            </footer>
        </td>
    </tr>
</table>
</body>

</html>
