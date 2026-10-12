<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>تذكير بالتحدي</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            direction: rtl;
            text-align: right;
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
        <h1>تذكير بالتحدي</h1>
    </div>
    <div class="email-body">
        <h2>عزيزي ${user.fullName},</h2>
        <p>هذا تذكير بالتحدي القادم:</p>
        <h3>${challenge.titleEn}</h3>
        <p>${challenge.descriptionEn}</p>
        <p>سيبدأ التصويت على التحدي في:</p>
        <p><strong>${challenge.startTime}</strong></p>
        <p>يرجى النقر على الزر أدناه لعرض التحدي والمشاركة:</p>
        <a href="${myChallengesUrl}" class="button">عرض التحدي</a>
        <p>نحن نتطلع إلى مشاركتك!</p>
        <p>أطيب التحيات,</p>
        <p>فريق التحدي</p>
    </div>
    <div class="email-footer">
        <p>&copy; 2023 شرطة دبي. جميع الحقوق محفوظة.</p>
        <p>إذا كان لديك أي أسئلة، يرجى الاتصال بنا على <a href="mailto:eng.mo.abdu@gmail.com">eng.mo.abdu@gmail.com</a>
        </p>
    </div>
</div>
</body>
</html>
