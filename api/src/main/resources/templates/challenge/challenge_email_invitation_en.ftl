<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Challenge Invitation</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
            background-color: #f2f2f2;
            text-align: center;
        }

        header {
            background-color: #4CAF50;
            color: white;
            padding: 20px;
            text-align: center;
        }

        footer {
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            position: absolute;
            bottom: 0;
            width: 100%;
            text-align: center;
        }

        .container {
            max-width: 600px;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            direction: ltr;
            text-align: center;
        }

        .table-container {
            margin: 0 auto;
            width: 80%;
        }

        .table-content-container {
            display: table;
            margin: 0 auto;
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
        }

        .accept-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
        }
    </style>
</head>
<body>

<header>
    <img src="cid:logo.ico" alt="logo" style="max-width: 100%;">
</header>

<div class="container">
    <h2>Challenge Invitation</h2>

    <p>Welcome ${user.fullName},</p>

    <p>You have been invited to participate in the following challenge:</p>

    <h3>${challenge.titleEn}</h3>

    <div class="table-container">
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

    <p>Additional information about the challenge comes here...</p>

    <p>Thank you and looking forward to your participation!</p>

    <a href="${ChallengeUserInvitationUrl}" class="accept-button">Accept Invitation</a>
</div>

<footer>
    <p>&copy; 2023 Dubai Police. All rights reserved.</p>
</footer>

</body>
</html>
