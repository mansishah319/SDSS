<!DOCTYPE html>
<html lang="ar" dir="rtl">

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
            direction: rtl;
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
                    <h2>طلب التحقق من التحدي</h2>
                    <p>مرحبًا ${user.fullName},</p>
                    <p>لديك طلب للتحقق من التحدي التالي:</p>
                    <h3>${challenge.titleAr}</h3>
                    <div class="table-content-container">
                        <div class="table-row">
                            <div class="table-cell-label">التاريخ:</div>
                            <div class="table-cell-value">${challenge.startTime}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-cell-label">بداية الوقت:</div>
                            <div class="table-cell-value">${challenge.startTime}</div>
                        </div>
                        <div class="table-row">
                            <div class="table-cell-label">نهاية الوقت:</div>
                            <div class="table-cell-value">${challenge.endTime}</div>
                        </div>
                    </div>
                </div>
                <p>معلومات إضافية حول طلب التحقق تأتي هنا...</p>
                <p>شكرًا لاهتمامك وتعاونك السريع!</p>
                <a href="${ChallengeUserInvitationUrl}" class="accept-button">View Validation Request</a>
            </div>
            <footer>
                <p>&copy; 2023 شرطة دبي جميع الحقوق محفوظة</p>
            </footer>
        </td>
    </tr>
</table>
</body>

</html>
