package com.sdi.castivate.utils;


public class HttpUri {


    // sdi php server service URL

//    public static String CastingURL="http://casting.sdiphp.com/castingNew/public/";

    //  Live php server service URL

    //Development Url
    //http://casting.sdiphp.com/castingNew/public/index.php/casting?
    //public static String IMAGE_URL= "http://castivate.com/castingNew/public/index.php/getImagesNewAndroid/";


    //Live
    //public static String CastingURL="http://castivate.com/castingNewVer1/public/" ;

//    public static String CastingURL="http://114.69.235.57:9998/castivate/castingNewVer1/public/" ;

    public static String CastingURL= "http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/";


    //    "http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/"


    //raghul public static String CastingURL="http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/" ;
    public static String LocalCastingURL = "http://114.69.235.57:9998/castivate/castingNewVer1/public/";
    //http://114.69.235.57:9998/castivate/castingNewVer1/public/
    //public static String IMAGE_URL= "http://castivate.com/castingNewVer1/public/index.php/getImagesNew/";
    //public static String IMAGE_URL= "http://114.69.235.57:9998/castivate/castingNewVer1/public/index.php/getImagesNew/";
    /*http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/setCastingFav*/
    public static String IMAGE_URL = "http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public/index.php/getImagesNew/";

    //Devep
    //   public static String IMAGE_URL= "http://casting.sdiphp.com/castingNew/public/index.php/getImagesNewAndroid/";
//    public static String CastingURL="http://casting.sdiphp.com/castingNew/public/index.php/";


    public static String CASTING = CastingURL + "casting?";
    public static String SUBMITCASTING = CastingURL + "submitCasting";
    public static String GETUSERID = CastingURL + "getUserId?";
    public static String SETTOKEN = CastingURL + "setToken?";
    public static String SETNOTIFICATION = CastingURL + "setNotification?";
    public static String GETNOTIFICATION = CastingURL + "getNotification?";
    public static String SET_FAV = CastingURL + "setCastingFav?";
    public static String GET_FAV = CastingURL + "favcasting?";
    public static String GET_USER_PROFILE = CastingURL + "getUserProfile?";
    public static String SUBMIT_IMAGE = CastingURL + "submitImageNew?";

    //Sugumaran
    public static String SET_IMG_FAV = CastingURL + "setImageFav?";
    public static String GET_IMG_FAV = CastingURL + "getImageFav?";

    //new  add this 17/08/2016
    public static String CASTING_REGISTRATION = CastingURL + "insertPerson?";
    public static String CASTING_SIGNIN = CastingURL + "personlogin?";
    public static String CASTING_FORGOT_PASSWORD = CastingURL + "forgotPassword?";
    public static String CASTING_FILE_UPLOAD = CastingURL + "fileupload";

    //Nijamudhin
    public static String GET_AUTO_SUBMIT = CastingURL + "getAutoSubmitStatus?";
    public static String UPDATE_AUTO_SUBMIT = CastingURL + "updateAutoSubmitStatus?";

    // get the profile url
    public static String CASTING_PROFILE_UPDATE = CASTING + "personProfile";

    public static String REPORT_ABUSE= CastingURL + "reportAbuse?";

//http://114.69.235.57:9998/castivate/castingNewVer1/public/fileupload


//    http://castivate.com/castingNew/public/casting?
//    	 http://castivate.com/castingNew/public/setCastingFav?


    //http://castivate.com/castingNew/public/getImageFav


//    public static String SUBMIT_IMAGE_NEW= "submitImageNew?";


//	public static String IMAGE_URL= "http://castivate.com/castingNew/public/index.php/getImages/";
    //public static String IMAGE_URL= "http://castivate.com/castingNew/public/getImagesAndroid/";


    //public static String IMAGE_URL= "http://castivate.com/castingNew/public/getImagesNew/";


}
