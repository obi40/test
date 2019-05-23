<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <title>Email</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <style>
        body {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        table {
            border-collapse: collapse;
            width: 500px;
        }

        .main-color {
            background-color: rgb(36, 112, 156);
        }
        
        .logo {
            float: left;
            padding-top: 10px;
            padding-bottom: 10px;
            height: 50px;
            display: block;
            margin-left: 25px;

        }

        .content-row {
            background-color: rgba(245, 245, 245, 1);
        }

        p {
            margin-left: 25px;
            margin-right: 25px;
        }

        .dense-p {
            margin-top: 8px;
            margin-bottom: 8px;
        }

        .credentials {
            font-style: italic;
        }

        .contact-us{
            color: #e3ecf2 !important;
        }
        .contact-us a{
            color: #e3ecf2 !important;
        } 

    </style>
</head> 
<body>
    <table align="center" border="0" cellpadding="0" cellspacing="0">
        <tr class="main-color">
            <td align="center">
                <img src="${logo}" class="logo row-margin" title="AccuLab Logo" alt="AccuLab Logo" style="display: block;" width="150" height="50"  />
            </td>
        </tr>
        <tr class="content-row">
            <td>
                <p class="dense-p">Dear ${name},</p>
            </td>
        </tr>
        <tr class="content-row">
            <@layout.block name="contents">
            </@layout.block>
        </tr>
        <tr class="main-color contact-us">
            <td>
                <p class="dense-p">Thanks</p>
                <p class="dense-p">Contact Us ${adminEmail}.</p>
                <p class="dense-p">&copy; 2019
                    <a href="http://optimiza.me">Optimiza</a> Solutions, All rights reserved.</p>
            </td>
        </tr>
    </table>
</body>
</html>