package com.power.travel.xixuntravel.net;

public class HttpUrl {


	public static String Url="http://xizanglvyou.ip115.enet360.com";
	public static String keystr="TibetEnet3602016";
	
	public static String sendmessages=Url+"/Api/Oauth/sendmessages.html";//短信接口
	public static String register=Url+"/Api/OauthMember/register.html";//注册接口
	public static String Agreement=Url+"/Api/Service/Agreement.html";//注册协议
//	public static String uploadify=Url+"/Api/Uploads/uploadify.html";//上传图片
	public static String uploadify=Url+"/Api/Upload/uploadify.html";//上传图片
	public static String video=Url+"/Api/Upload/video.html";//上传视频
	public static String login=Url+"/Api/OauthMember/login.html";//登陆
	public static String openidLogin=Url+"/Api/OauthMember/openidLogin.html";//第三方登陆 判断是否绑定
	public static String openidBinding=Url+"/Api/OauthMember/openidBinding.html";//第三方登陆 绑定
	public static String index=Url+"/Api/OauthMember/index.html";//用户信息
	public static String edit_info=Url+"/Api/OauthMember/edit_info.html";//修改 用户信息
	public static String Service_province=Url+"/Api/Service/Service_province.html";//省市区
	public static String Service_feedback=Url+"/Api/Service/Service_feedback.html";//意见反馈
	public static String Service_about=Url+"/Api/Service/Service_about.html";// 5公司简介 7联系我们
	public static String forgotpass=Url+"/Api/OauthMember/forgotpass.html";//找回密码
	public static String changepwd=Url+"/Api/OauthMember/modifyPass.html";//修改密码
	public static String openidReg=Url+"/Api/OauthMember/openidReg";//绑定三方，创建密码
	public static String apply_guide=Url+"/Api/OauthMember/apply_guide";//申请导游
	public static String apply_driver=Url+"/Api/OauthMember/apply_driver";//申请司机
	public static String knowledge=Url+"/Api/Service/knowledge.html";//常识
	public static String rescuenews=Url+"/Api/Service/rescuenews.html";//救援知识
	public static String rescuetel=Url+"/Api/Service/rescuetel.html";//救援电话 ，租车 ，医院  ，派出所（√）
	public static String master=Url+"/Api/Service/master.html";//地主达人
	public static String guide=Url+"/Api/Service/guide.html";//导游
	public static String driver=Url+"/Api/Service/driver.html";//司机
	public static String viewspot=Url+"/Api/Service/viewspot.html";//景区
	public static String viewinfo=Url+"/Api/Service/viewinfo.html";//景区详情
	public static String viewcommentlist=Url+"/Api/Service/viewcommentList.html";//景区评论列表
	public static String viewcomment=Url+"/Api/Service/viewcomment.html";//景区评论
	public static String Route=Url+"/Api/Service/Route.html";//推荐路线
	public static String RouteDetails=Url+"/Api/Service/RouteDetails.html";//推荐路线详情
	public static String Recommend=Url+"/Api/Service/Recommend.html";//推荐
	public static String recommendDetails=Url+"/Api/Service/recommendDetails.html";//推荐详情
	public static String recommendZan=Url+"/Api/Service/recommendZan.html";//推荐点赞 取消赞
	public static String add=Url+"/Api/Travels/add.html";//发布游记
	public static String my_lists=Url+"/Api/Travels/my_lists.html";//我的游记
	public static String system_message=Url+"/Api/OauthMember/system_message";//系统消息
	public static String message=Url+"/Api/OauthMember/message.html";//消息
	public static String message_type=Url+"/Api/OauthMember/messageState.html";//消息标记为 已读
	public static String addTrip=Url+"/Api/Trip/add.html";//发布行程
	public static String my_triplists=Url+"/Api/Trip/my_lists.html";//我的行程
	public static String all_triplists=Url+"/Api/Trip/lists.html";//全部行程/行程筛选
	public static String all_tripdetail=Url+"/Api/Trip/contents.html";//全部行程/行程筛选  详情
	public static String comment=Url+"/Api/Trip/comment.html";//行程评论列表
	public static String addcomment=Url+"/Api/Trip/addcomment.html";//行程添加评论
	public static String all_triavellists=Url+"/Api/Travels/lists.html";//全部游记/游记筛选
	public static String all_triaveldetail=Url+"/Api/Travels/contents.html";//全部游记/游记筛选  详情
	public static String delete_my_travel=Url+"/Api/Travels/delete.html";//删除我的游记
	public static String all_triavelpraise=Url+"/Api/Travels/praise.html";//全部游记/游记筛选  赞 取消赞
	public static String all_triavelcomment=Url+"/Api/Travels/addcomment.html";//全部游记/游记筛选 评论
	public static String delete_my_triplists=Url+"/Api/Trip/del.html";//删除我的行程
	public static String recovery_my_triplists=Url+"/Api/Trip/recovery.html";//恢复我的行程
	public static String cancel_my_triplists=Url+"/Api/Trip/cancel.html";//取消我的行程
	public static String my_follow=Url+"/Api/OauthMember/my_follow.html";//关注
	public static String followAdd=Url+"/Api/OauthMember/followAdd.html";//关注
	public static String follow_cancel=Url+"/Api/OauthMember/my_follow_cancel";//取消关注
	public static String search=Url+"/Api/Service/search.html";//搜索
	public static String Car=Url+"/Api/Service/Car.html";//车型品牌
	public static String ad=Url+"/Api/Service/ad.html";//约伴顶部广告
	public static String age=Url+"/Api/Service/age.html";//年龄 驾龄获取
	public static String nation=Url+"/Api/Service/nation.html";//民族获取
	public static String download=Url+"/Api/Service/download.html";//app自动升级
	public static String nearbyPeople=Url+"/Api/OauthMember/NearbyPeople";//附近人
	public static String addcarRental=Url+"/Api/CarRental/AddTo";//发布租车
	public static String addRental = Url+"/Api/Rental/AddTo";//发布租房
	public static String carRentalList = Url+"/Api/CarRental/lists"; //租车列表
	public static String rentalList = Url+"/Api/Rental/lists"; //租房列表
	public static String carRentalFollow = Url+"/Api/CarRental/follow";//租车关注
	public static String carRentalContent = Url+"/Api/CarRental/content";//租车详情
	public static String rentalContent = Url+"/Api/Rental/content";//租房详情
	public static String rentalFollow = Url+"/Api/Rental/follow";//租房关注
	public static String carRentalComment = Url+"/Api/CarRental/comment";//租车评论列表
	public static String carRentalCommentAdd = Url+"/Api/CarRental/commentAdd";//租车发布评论
    public static String rentalComment = Url+"/Api/Rental/comment";//租房评论列表
    public static String centalCommentAdd = Url+"/Api/Rental/commentAdd";//租房发布评论
	public static String rentdata = Url+"/Api/Oauth/rentdata";//租金区间
}
