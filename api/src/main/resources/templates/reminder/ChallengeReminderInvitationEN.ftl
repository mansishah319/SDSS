<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Challenge Reminder</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        .email-container {
            width: 100%;
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
            border: 1px solid #ddd;
            border-radius: 5px;
            overflow: hidden;
        }

        .email-header {
            background-color: #007bff;
            color: #ffffff;
            padding: 20px;
            text-align: center;
        }

        .email-header h1 {
            margin: 0;
            font-size: 24px;
        }

        .email-body {
            padding: 20px;
        }

        .email-body h2 {
            font-size: 20px;
            color: #333333;
        }

        .email-body p {
            font-size: 16px;
            color: #555555;
        }

        .email-footer {
            background-color: #f4f4f4;
            color: #777777;
            padding: 10px;
            text-align: center;
            font-size: 12px;
        }

        .email-footer p {
            margin: 0;
        }

        .button {
            display: inline-block;
            padding: 10px 20px;
            margin-top: 20px;
            font-size: 16px;
            color: #ffffff;
            background-color: #007bff;
            text-decoration: none;
            border-radius: 5px;
        }

        .button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
<div class="email-container">
    <div class="email-header">
        <h1>Challenge Reminder</h1>
    </div>
    <div class="email-body">
        <h2>Dear ${user.fullName},</h2>
        <p>This is a reminder for the upcoming challenge voting Stage:</p>
        <h3>${challenge.titleEn}</h3>
        <p>${challenge.descriptionEn}</p>
        <p>The challenge will start at:</p>
        <p><strong>${challenge.startTime}</strong></p>
        <p>Please click the button below to view the challenge and participate:</p>
        <a href="${myChallengesUrl}" class="button">View Challenge</a>
        <p>We look forward to your participation!</p>
        <p>Best regards,</p>
        <p>The Challenge Team</p>
    </div>
    <div class="email-footer">
        <p>&copy; 2023 Dubai Police. All rights reserved.</p>
        <p>If you have any questions, please contact us at <a
                    href="mailto:eng.mo.abdu@gmail.com">eng.mo.abdu@gmail.com</a></p>
    </div>
</div>
</body>
</html>
