package com.bnade.wow.catcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bnade.util.Mail;
import com.bnade.wow.client.model.JAuction;
import com.bnade.wow.dao.UserDao;
import com.bnade.wow.dao.impl.UserDaoImpl;
import com.bnade.wow.po.UserItemNotification;

public class AuctionItemNotificationTask {	
	
	private static Logger logger = LoggerFactory.getLogger(AuctionItemNotificationTask.class);
	private static ExecutorService pool = Executors.newFixedThreadPool(10);
	private UserDao userDao;
	
	public AuctionItemNotificationTask() {
		userDao = new UserDaoImpl();
	}

	public void process(Map<String, JAuction> minByoutAuctions, int realmId, long lastModified) {
		pool.execute(() -> {
			try {
				List<UserItemNotification> itemNs = userDao.getItemNotificationsByRealmId(realmId);
				Map<Integer, List<UserItemNotification>> matchedItems = new HashMap<>();
				logger.info("找到{}条服务器{}的物品通知", itemNs.size(), realmId);
				for (UserItemNotification itemN : itemNs) {
					String key = "" + itemN.getItemId() + 0 + 0 + "";
					JAuction auc = minByoutAuctions.get(key);
					if (auc != null) {
						if (itemN.getIsInverted() == 0) { // 低于
							if (auc.getBuyout() <= itemN.getPrice()) {
								itemN.setMinBuyout(auc.getBuyout());
								List<UserItemNotification> tmpList = matchedItems.get(itemN.getUserId());
								if (tmpList == null) {
									tmpList = new ArrayList<>();
									tmpList.add(itemN);
									matchedItems.put(itemN.getUserId(), tmpList);
								} else {
									tmpList.add(itemN);
								}
							}
						}
						if (itemN.getIsInverted() == 1) { // 高于
							if (auc.getBuyout() >= itemN.getPrice()) {
								itemN.setMinBuyout(auc.getBuyout());
								List<UserItemNotification> tmpList = matchedItems.get(itemN.getUserId());
								if (tmpList == null) {
									tmpList = new ArrayList<>();
									tmpList.add(itemN);
									matchedItems.put(itemN.getUserId(), tmpList);
								} else {
									tmpList.add(itemN);
								}
							}
						}
					}
				}
				pushNotification(matchedItems, realmId);
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		});
	}
	
	private void pushNotification(Map<Integer, List<UserItemNotification>> matchedItems, int realmId) {
		if (matchedItems.size() > 0) {
			logger.info("开始推送{}条服务器[{}]", matchedItems.size(), realmId);
			for (Map.Entry<Integer, List<UserItemNotification>> entry : matchedItems.entrySet()) {
				List<UserItemNotification> items = entry.getValue();
				String mail = null;
				String mailContent = "";
				for (UserItemNotification item : items) {
					mailContent += "<h3>";
					mail = item.getEmail();
					mailContent += item.getItemName() + " 当前最低一口单价：" + getGold(item.getMinBuyout());
					if (item.getIsInverted() == 0) {
						mailContent += "低与您的价格：" + getGold(item.getPrice());
					} else {
						mailContent += "高与您的价格：" + getGold(item.getPrice());
					}
					mailContent += "</h3>";
				}
				Mail.asynSendHtmlEmail("[BNADE] " + items.size() + "条物品满足在[" + realmMap.get(realmId) + "]", mailContent, mail);
			}
			
		}		
	}

	private String getGold(long price) {
		String s = "";
		long gold = price/10000;
		if (gold > 0) {
			s += gold + "g";
			price -= gold * 10000;
		}
		long silver = price / 100;
		if (silver > 0) {
			s += silver + "s";
			price -= silver * 100;
		}
		if (price > 0) {
			s += price + "c";
		}
		return s;
	}
	
	private static Map<Integer, String> realmMap = new HashMap<>();
	static {
		realmMap.put(1, "万色星辰-奥蕾莉亚-世界之树-布莱恩");
		realmMap.put(2, "丹莫德-克苏恩");
		realmMap.put(3, "主宰之剑-霍格");
		realmMap.put(4, "丽丽-四川");
		realmMap.put(5, "亚雷戈斯-银松森林");
		realmMap.put(6, "亡语者");
		realmMap.put(7, "伊兰尼库斯-阿克蒙德-恐怖图腾");
		realmMap.put(8, "伊利丹-尘风峡谷");
		realmMap.put(9, "伊森利恩");
		realmMap.put(10, "伊森德雷-达斯雷玛-库尔提拉斯-雷霆之怒");
		realmMap.put(11, "伊瑟拉-艾森娜-月神殿-轻风之语");
		realmMap.put(12, "伊莫塔尔-萨尔");
		realmMap.put(13, "伊萨里奥斯-祖阿曼");
		realmMap.put(14, "元素之力-菲米丝-夏维安");
		realmMap.put(15, "克尔苏加德");
		realmMap.put(16, "克洛玛古斯-金度");
		realmMap.put(17, "军团要塞-生态船");
		realmMap.put(18, "冬拥湖-迪托马斯-达基萨斯");
		realmMap.put(19, "冬泉谷-寒冰皇冠");
		realmMap.put(20, "冰川之拳-双子峰-埃苏雷格-凯尔萨斯");
		realmMap.put(21, "冰霜之刃-安格博达");
		realmMap.put(22, "冰风岗");
		realmMap.put(23, "凤凰之神-托塞德林");
		realmMap.put(24, "凯恩血蹄-瑟莱德丝-卡德加");
		realmMap.put(25, "利刃之拳-黑翼之巢");
		realmMap.put(26, "刺骨利刃-千针石林");
		realmMap.put(27, "加兹鲁维-奥金顿-哈兰");
		realmMap.put(28, "加基森-黑暗虚空");
		realmMap.put(29, "加尔-黑龙军团");
		realmMap.put(30, "加里索斯-库德兰");
		realmMap.put(31, "勇士岛-达文格尔-索拉丁");
		realmMap.put(32, "卡德罗斯-符文图腾-黑暗魅影-阿斯塔洛");
		realmMap.put(33, "卡扎克-爱斯特纳-戈古纳斯-巴纳扎尔");
		realmMap.put(34, "卡拉赞-苏塔恩");
		realmMap.put(35, "卡珊德拉-暗影之月");
		realmMap.put(36, "厄祖玛特-奎尔萨拉斯");
		realmMap.put(37, "古加尔-洛丹伦");
		realmMap.put(38, "古尔丹-血顶");
		realmMap.put(39, "古拉巴什-安戈洛-深渊之喉-德拉诺");
		realmMap.put(40, "古达克-梅尔加尼");
		realmMap.put(41, "哈卡-诺森德-燃烧军团-死亡熔炉");
		realmMap.put(42, "嚎风峡湾-闪电之刃");
		realmMap.put(43, "回音山-霜之哀伤-神圣之歌-遗忘海岸");
		realmMap.put(44, "国王之谷");
		realmMap.put(45, "图拉扬-海达希亚-瓦里玛萨斯-塞纳里奥");
		realmMap.put(46, "圣火神殿-桑德兰");
		realmMap.put(47, "地狱之石-火焰之树-耐奥祖");
		realmMap.put(48, "地狱咆哮-阿曼尼-奈法利安");
		realmMap.put(49, "埃克索图斯-血牙魔王");
		realmMap.put(50, "埃加洛尔-鲜血熔炉-斩魔者");
		realmMap.put(51, "埃基尔松");
		realmMap.put(52, "埃德萨拉");
		realmMap.put(53, "埃雷达尔-永恒之井");
		realmMap.put(54, "基尔加丹-奥拉基尔");
		realmMap.put(55, "基尔罗格-巫妖之王-迦顿");
		realmMap.put(56, "塔纳利斯-巴瑟拉斯-密林游侠");
		realmMap.put(57, "塞拉摩-暗影迷宫-麦姆");
		realmMap.put(58, "塞拉赞恩-太阳之井");
		realmMap.put(59, "塞泰克-罗曼斯-黑暗之矛");
		realmMap.put(60, "壁炉谷");
		realmMap.put(61, "外域-织亡者-阿格拉玛-屠魔山谷");
		realmMap.put(62, "大地之怒-恶魔之魂-希尔瓦娜斯");
		realmMap.put(63, "大漩涡-风暴之怒");
		realmMap.put(64, "天空之墙");
		realmMap.put(65, "天谴之门");
		realmMap.put(66, "夺灵者-战歌-奥斯里安");
		realmMap.put(67, "奈萨里奥-红龙女王-菲拉斯");
		realmMap.put(68, "奎尔丹纳斯-艾莫莉丝-布鲁塔卢斯");
		realmMap.put(69, "奥妮克希亚-海加尔-纳克萨玛斯");
		realmMap.put(70, "奥尔加隆");
		realmMap.put(71, "奥杜尔-普瑞斯托-逐日者");
		realmMap.put(72, "奥特兰克");
		realmMap.put(73, "奥达曼-甜水绿洲");
		realmMap.put(74, "守护之剑-瑞文戴尔");
		realmMap.put(75, "安东尼达斯");
		realmMap.put(76, "安其拉-弗塞雷迦-盖斯");
		realmMap.put(77, "安加萨-莱索恩");
		realmMap.put(78, "安威玛尔-扎拉赞恩");
		realmMap.put(79, "安纳塞隆-日落沼泽-风暴之鳞-耐普图隆");
		realmMap.put(80, "安苏");
		realmMap.put(81, "山丘之王-拉文霍德");
		realmMap.put(82, "巨龙之吼-黑石尖塔");
		realmMap.put(83, "巴尔古恩-托尔巴拉德");
		realmMap.put(84, "布兰卡德");
		realmMap.put(85, "布莱克摩-灰谷");
		realmMap.put(86, "希雷诺斯-芬里斯-烈焰荆棘");
		realmMap.put(87, "幽暗沼泽");
		realmMap.put(88, "影之哀伤");
		realmMap.put(89, "影牙要塞-艾苏恩");
		realmMap.put(90, "恶魔之翼-通灵学院");
		realmMap.put(91, "戈提克-雏龙之翼");
		realmMap.put(92, "拉文凯斯-迪瑟洛克");
		realmMap.put(93, "拉格纳洛斯-龙骨平原");
		realmMap.put(94, "拉贾克斯-荆棘谷");
		realmMap.put(95, "提尔之手-萨菲隆");
		realmMap.put(96, "提瑞斯法-暗影议会");
		realmMap.put(97, "摩摩尔-熵魔-暴风祭坛");
		realmMap.put(98, "斯坦索姆-穆戈尔-泰拉尔-格鲁尔");
		realmMap.put(99, "无尽之海-米奈希尔");
		realmMap.put(100, ");无底海渊-阿努巴拉克-刀塔-诺莫瑞根");
		realmMap.put(101, ");时光之穴");
		realmMap.put(102, ");普罗德摩-铜龙军团");
		realmMap.put(103, ");晴日峰-江苏");
		realmMap.put(104, ");暗影裂口");
		realmMap.put(105, ");暮色森林-杜隆坦-狂风峭壁-玛瑟里顿");
		realmMap.put(106, ");月光林地-麦迪文");
		realmMap.put(107, ");末日祷告祭坛-迦罗娜-纳沙塔尔-火羽山");
		realmMap.put(108, ");末日行者");
		realmMap.put(109, ");朵丹尼尔-蓝龙军团");
		realmMap.put(110, ");格瑞姆巴托-埃霍恩");
		realmMap.put(111, ");格雷迈恩-黑手军团-瓦丝琪");
		realmMap.put(112, ");梦境之树-诺兹多姆-泰兰德");
		realmMap.put(113, ");森金-沙怒-血羽");
		realmMap.put(114, ");死亡之翼");
		realmMap.put(115, ");毁灭之锤-兰娜瑟尔");
		realmMap.put(116, ");永夜港-翡翠梦境-黄金之路");
		realmMap.put(117, ");沃金");
		realmMap.put(118, ");法拉希姆-玛法里奥-麦维影歌");
		realmMap.put(119, ");洛肯-海克泰尔");
		realmMap.put(120, ");洛萨-阿卡玛-萨格拉斯");
		realmMap.put(121, ");深渊之巢");
		realmMap.put(122, ");激流之傲-红云台地");
		realmMap.put(123, ");激流堡-阿古斯");
		realmMap.put(124, ");火喉-雷克萨");
		realmMap.put(125, ");火烟之谷-玛诺洛斯-达纳斯");
		realmMap.put(126, ");烈焰峰-瓦拉斯塔兹");
		realmMap.put(127, ");熊猫酒仙");
		realmMap.put(128, ");熔火之心-黑锋哨站");
		realmMap.put(129, ");燃烧之刃");
		realmMap.put(130, ");燃烧平原-风行者");
		realmMap.put(131, ");狂热之刃");
		realmMap.put(132, ");玛多兰-银月-羽月-耳语海岸");
		realmMap.put(133, ");玛洛加尔");
		realmMap.put(134, ");玛里苟斯-艾萨拉");
		realmMap.put(135, ");瓦拉纳");
		realmMap.put(136, ");白银之手");
		realmMap.put(137, ");白骨荒野-能源舰");
		realmMap.put(138, ");石爪峰-阿扎达斯");
		realmMap.put(139, ");石锤-范达尔鹿盔");
		realmMap.put(140, ");破碎岭-祖尔金");
		realmMap.put(141, ");祖达克-阿尔萨斯");
		realmMap.put(142, ");索瑞森-试炼之环");
		realmMap.put(143, ");红龙军团");
		realmMap.put(144, ");罗宁");
		realmMap.put(145, ");自由之风-达隆米尔-艾欧纳尔-冬寒");
		realmMap.put(146, ");艾维娜-艾露恩");
		realmMap.put(147, ");范克里夫-血环");
		realmMap.put(148, ");萨洛拉丝");
		realmMap.put(149, ");藏宝海湾-阿拉希-塔伦米尔");
		realmMap.put(150, ");蜘蛛王国");
		realmMap.put(151, ");血吼-黑暗之门");
		realmMap.put(152, ");血色十字军");
		realmMap.put(153, ");贫瘠之地");
		realmMap.put(154, ");踏梦者-阿比迪斯");
		realmMap.put(155, ");辛达苟萨");
		realmMap.put(156, ");达克萨隆-阿纳克洛斯");
		realmMap.put(157, ");达尔坎-鹰巢山");
		realmMap.put(158, ");迅捷微风");
		realmMap.put(159, ");远古海滩");
		realmMap.put(160, ");迦拉克隆");
		realmMap.put(161, ");迦玛兰-霜狼");
		realmMap.put(162, ");金色平原");
		realmMap.put(163, ");阿拉索-阿迦玛甘");
		realmMap.put(164, ");雷斧堡垒");
		realmMap.put(165, ");雷霆之王");
		realmMap.put(166, ");雷霆号角-风暴之眼");
		realmMap.put(167, ");风暴峭壁");
		realmMap.put(168, ");鬼雾峰");
		realmMap.put(169, ");黑铁");
		realmMap.put(170, ");斯克提斯");
	}
}
