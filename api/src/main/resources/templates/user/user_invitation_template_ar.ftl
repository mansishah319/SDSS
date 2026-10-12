<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>تفعيل الحساب</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }

        .container {
            background-color: rgba(255, 255, 255, 0.8);
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
        }

        .header {
            background-color: #1c6804;
            color: #fff;
            padding: 10px;
        }

        .header img {
            max-width: 60px; /* Increase the logo size */
            display: block;
            margin: 0 auto; /* Center the image horizontally */
        }

        h1 {
            color: #333;
        }

        p {
            color: #666;
        }

        .activation-button {
            display: inline-block;
            background-color: #1c6804;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
        }

        .activation-button:hover {
            background-color: #0056b3;
        }

        .footer {
            background-color: #333;
            color: #fff;
            padding: 10px;
        }
    </style>
</head>
<body>
<div class="main">
    <div class="header">
        <img src="cid:logo.ico" alt="logo">
    </div>
    <div class="container">
        <h1>مرحبًا بك في منصتنا</h1>
        <p>مرحبًا ${user.fullName},</p>
        <p>لقد تلقيت دعوة من نظام دعم القرار الذكي للانضمام إلى منصتنا.</p>
        <p>انقر على الزر أدناه لتفعيل حسابك:</p>
        <a class="activation-button" href="${userUrl}">تفعيل الحساب</a>
    </div>
    <div class="footer">
        &copy; 2023 شرطة دبي. جميع الحقوق محفوظة.
    </div>
</div>
</body>
</html>
