function IsMobilePhone(num) {
    var isMobilePhone = /^1[3|4|5|7|8][0-9]\d{8}$/;
    if (isMobilePhone.test(num))
        return true;
    else
        return false;
}