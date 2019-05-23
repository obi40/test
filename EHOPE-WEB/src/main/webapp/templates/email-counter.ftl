<@layout.extends name="email-base.ftl">
    <@layout.put block="contents"> 
        <td>
            <p>We are notifying you about your used amount of criteria because one or more is/are below our grace percentage:</p>
            <p>- Orders ${ordersCurrent} / ${ordersMaxAmount}</p>
            <p>- Users ${usersCurrent} / ${usersMaxAmount}</p>
        </td>
    </@layout.put>
</@layout.extends> 