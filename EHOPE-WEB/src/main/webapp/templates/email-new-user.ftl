<@layout.extends name="email-base.ftl">
    <@layout.put block="contents"> 
                    <td>
                <p>Welcome to Acculab.</p>
                <p>
                    <a href="${loginUrl}">Login</a> to Acculab using these credentials:</p>
                <p>
                    username:
                    <b>${username}</b>
                </p>
                <p> password:
                    <b>${password}</b>
                </p>

                <p>Click on
                    <a href="${userProfileUrl}">User profile</a> after logging to change the password.
                </p>
            </td>
    </@layout.put>
</@layout.extends>