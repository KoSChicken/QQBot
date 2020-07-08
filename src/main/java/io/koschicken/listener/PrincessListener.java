package io.koschicken.listener;

import org.springframework.stereotype.Service;

@Service
public class PrincessListener {
//
//    @Autowired
//    TeamMemberService teamMemberServiceImpl;
//    @Autowired
//    KnifeListService knifeListServiceImpl;
//    @Autowired
//    ProgressService ProgressServiceImpl;
//    @Autowired
//    TreeService treeServiceImpl;
//    @Autowired
//    PcrUnionService pcrUnionServiceImpl;
//
//    //处理交刀的数据
//    public static KnifeState toHurt(long groupQQ, long QQ, int hurt, KnifeListService knifeListServiceImpl, ProgressService progressServiceImpl, TreeService treeServiceImpl) {
//        StringBuilder stringBuilder = new StringBuilder();
//        Progress progress = progressServiceImpl.getProgress(groupQQ);
//        KnifeList knifeList;
//        KnifeState knifeState = new KnifeState();
//        if (progress != null) {//没有公会boss进度数据
//            if (progress.getStartTime().isBefore(LocalDateTime.now())) { //时间若已过开始则可以上报伤害
//                //会战开启 上报伤害
//                //查询这个人出没出完三刀
//                if (knifeListServiceImpl.getKnifeNum(QQ, LocalDateTime.now(), true) > 2) {
//                    knifeState.setOk(false);
//                    knifeState.setFailMsg("三刀已出完，不可再出了");
//                    return knifeState;
//                }
//
//                try {
//                    treeServiceImpl.removeById(QQ);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                knifeList = new KnifeList();//创建刀数据对象
//                knifeList.setKnifeQQ(QQ);
//                knifeList.setLoop(progress.getLoop());
//                knifeList.setPosition(progress.getSerial());
//                knifeList.setDate(LocalDateTime.now());
//                //计算boss血量，分成打爆处理（有救树流程）和没打爆处理
//
//                if (progress.getRemnant() - hurt > 0) {
//                    knifeList.setComplete(true);
//                    knifeList.setHurt(hurt);
//                    progress.setRemnant(progress.getRemnant() - hurt);
//                    //没打穿boss
//                } else {
//                    //先记录血量
//                    knifeList.setHurt(progress.getRemnant());//伤害值为boss血量
//                    //伤害打穿了，进入下一模式
//                    int loop = (progress.getSerial() == 5 ? progress.getLoop() + 1 : progress.getLoop());
//                    int serial = (progress.getSerial() == 5 ? 1 : progress.getSerial() + 1);
//                    progress.setLoop(loop);
//                    progress.setSerial(serial);
//                    progress.setRemnant(BossHpLimit[serial - 1]);
//
//                    //进入救树模式，把树上的人都噜下来
//                    List<Tree> strings = treeServiceImpl.deletTreeByGroup(groupQQ);
//
//                    List<KnifeList> strins = knifeListServiceImpl.getKnife(QQ, LocalDateTime.now());
//                    //判断是不是补时刀
//                    knifeList.setComplete(strins.size() != 0 && !strins.get(strins.size() - 1).getComplete());
//                    stringBuilder.append("此刀为尾刀，下一刀将自动记为补时刀\n--------------");
//                    if (strings.size() > 0) {
//                        stringBuilder.append("下树啦，下树啦");
//                        for (Tree tree : strings) {
//                            stringBuilder.append("[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
//                        }
//                    }
//                    //记录这个刀的下一刀为补偿刀
//                    //这刀打爆了boss
//                }
//                //更新数据库数据
//                progressServiceImpl.updateById(progress);
//                knifeListServiceImpl.save(knifeList);
//
//                stringBuilder.append("现在boss状态:").append(progress.getLoop()).append("-").append(progress.getSerial()).append("\n");
//                stringBuilder.append("血量：").append(progress.getRemnant());
//            } else {
//                //会战还未开启
//                Duration duration = Duration.between(progress.getStartTime(), LocalDateTime.now());
//                knifeState.setOk(false);
//                knifeState.setFailMsg("会战将在" + duration.toDays() + "日" + duration.toHours() + "时后开启，还没有到打boss的时候哦");
//                return knifeState;
//            }
//        } else {
//            knifeState.setOk(false);
//            knifeState.setFailMsg("还没开始为什么就交刀惹");
//            return knifeState;
//        }
//        knifeState.setOk(true);
//        knifeState.setKnifeList(knifeList);
//        knifeState.setMsg(stringBuilder.toString());
//        return knifeState;
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#未出刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void searchVoidKnife(GroupMsg msg, MsgSender sender) {
//        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
//        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
//        HashMap<String, Integer> map;
//        StringBuilder src;
//        if (strings != null && strings.size() > 0) {
//            //有@人的
//            map = new HashMap<>();
//            for (String QQ : strings) {
//                map.put(QQ, 3 - knifeListServiceImpl.getKnifeNum(Long.parseLong(QQ), LocalDateTime.now(), true));
//            }
//        } else {
//            //公会全体
//            map = new HashMap<>();
//            List<Long> longs = teamMemberServiceImpl.getTeamMemberQQByGroup(msg.getGroupCodeNumber());
//            for (long l : longs) {
//                map.put(String.valueOf(l), 3 - knifeListServiceImpl.getKnifeNum(l, LocalDateTime.now(), true));
//            }
//        }
//        if (CollectionUtils.isEmpty(map)) {//没有空刀信息
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notBossOrNotDate);
//        } else {
//            Set<String> set = map.keySet();
//            src = new StringBuilder("统计如下:\n");
//            int flag;
//            for (String s : set) {
//                System.out.println(s);
//                flag = map.get(s);
//                if (flag > 0) {
//                    src.append("[CQ:at,qq=").append(s).append("] ,没出").append(flag).append("刀\n");
//                }
//            }
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), src.toString());
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#已出刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void searchKnife(GroupMsg msg, MsgSender sender) {
//        List<KnifeList> list = new ArrayList<>();
//        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
//        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
//        StringBuilder stringBuilder = new StringBuilder();
//        if (strings != null && strings.size() > 0) {
//            //找 人的出刀
//            for (String s : strings) {
//                list.addAll(knifeListServiceImpl.getKnife(cqAtoNumber(s), LocalDateTime.now()));
//            }
//        } else {
//            //找整个公会的出刀
//            list = knifeListServiceImpl.getKnifeList(msg.getGroupCodeNumber(), LocalDateTime.now());
//        }
//        if (list.size() > 0) {
//            stringBuilder.append("出刀信息：");
//            for (KnifeList knife : list) {
//                stringBuilder.append("\n-----\n编号: ").append(knife.getId());
//                stringBuilder.append("\n昵称：").append(teamMemberServiceImpl.getName(knife.getKnifeQQ()));
//                stringBuilder.append("\n扣扣：").append(knife.getKnifeQQ());
//                stringBuilder.append("\n伤害：").append(knife.getHurt());
//                stringBuilder.append("\n").append(knife.getLoop());
//                stringBuilder.append("-").append(knife.getPosition());
//            }
//            sender.SENDER.sendPrivateMsg(msg.getQQCode(), stringBuilder.toString());
//        } else {
//            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "还没有刀信息哦");
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"#出刀"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void getKnife(GroupMsg msg, MsgSender sender) {
//        if (!teamMemberServiceImpl.getGroupByQQ(msg.getCodeNumber()).equals(msg.getGroupCodeNumber())) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,他群间谍发现，建议rbq一周");
//        } else {
//            try {
//                //int i = ProgressServiceImpl.isFight(msg.getGroupCodeNumber());
//                try {
//                    Tree tree = new Tree();
//                    tree.setDate(LocalDateTime.now());
//                    tree.setGroupQQ(msg.getGroupCodeNumber());
//                    tree.setTeamQQ(msg.getCodeNumber());
//                    tree.setName(teamMemberServiceImpl.getName(msg.getCodeNumber()));
//                    tree.setTree(false);
//                    treeServiceImpl.save(tree);
//                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "出刀已记录，@会长看我表演");
//                } catch (UncategorizedSQLException e) {
//                    if (e.getSQLException().getErrorCode() == 19) {
//                        //主键冲突，
//                        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已经在出刀/挂树状态");
//                    }
//                }
//            } catch (BindingException e) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有开启公会战");
//            }
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#挂树", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void getTree(GroupMsg msg, MsgSender sender) {
//        try {
//            if (!teamMemberServiceImpl.getGroupByQQ(msg.getCodeNumber()).equals(msg.getGroupCodeNumber())) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,他群间谍发现，建议rbq一周");
//            } else {
//                int i = treeServiceImpl.updateTree(msg.getCodeNumber());
//                if (i == 1) {
//                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), isTree);
//                } else {
//                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有在树上");
//                }
//            }
//        } catch (NullPointerException e) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有加入公会");
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"#收刀", "#交刀"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void outKnife(GroupMsg msg, MsgSender sender) {
//        try {
//            int hurt = getHurt(msg.getMsg(), 1);
//
//            KnifeState knifeState = toHurt(msg.getGroupCodeNumber(), msg.getCodeNumber(), hurt, knifeListServiceImpl, ProgressServiceImpl, treeServiceImpl);
//            if (knifeState.isOk()) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), knifeState.getMsg());
//            } else {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), knifeState.getFailMsg());
//            }
//        } catch (NumberFormatException e) {
//            //伤害数字转换失败
//            e.printStackTrace();
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), commandError);
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#结束会战", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void endFight(GroupMsg msg, MsgSender sender) {
//        if (teamMemberServiceImpl.isAdmin(msg.getCodeNumber(), msg.getGroupCodeNumber())) {
//            Progress progress = ProgressServiceImpl.getProgress(msg.getGroupCodeNumber());
//            if (progress == null) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), EngFightStartDouble);
//                return;
//            }
//
//            ProgressServiceImpl.removeById(progress.getId());
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), SuccessEndFight);
//        } else {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
//        }
//    }
//
//    //撤刀 撤回刀的编号
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#撤刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void dropKnife(GroupMsg msg, MsgSender sender) {
//        if (teamMemberServiceImpl.isAdmin(msg.getCodeNumber(), msg.getGroupCodeNumber())) {
//            try {
//                int id = Integer.parseInt(msg.getMsg().replaceAll(" +", "").substring(3));
//                knifeListServiceImpl.removeById(id);
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "操作成功");
//            } catch (NumberFormatException e) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), commandError);
//            } catch (Exception e) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "删除失败");
//            }
//        } else {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
//        }
//    }
//
//    //调整boss状态 周目 几王 血量
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#调整boss状态", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void changeBoss(GroupMsg msg, MsgSender sender) {
//        if (teamMemberServiceImpl.isAdmin(msg.getCodeNumber(), msg.getGroupCodeNumber())) {
//            String[] change = msg.getMsg().replaceAll(" +", " ").split(" ");
//            boolean is;
//            Progress progress = ProgressServiceImpl.getProgress(msg.getGroupCodeNumber());
//            int loop = Integer.parseInt(change[1]);
//            int serial = Integer.parseInt(change[2]);
//            is = loop == progress.getLoop() && serial == progress.getSerial();
//            progress.setLoop(loop);
//            progress.setSerial(serial);
//            progress.setRemnant(Integer.valueOf(change[3]));
//
//            ProgressServiceImpl.updateById(progress);
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "boss调整成功");
//            if (!is) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "变更了周目，将还在出刀的人全部下树");
//            }
//        } else {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#开始会战", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void startFight(GroupMsg msg, MsgSender sender) {
//        try {
//            if (teamMemberServiceImpl.isAdmin(msg.getCodeNumber(), msg.getGroupCodeNumber())) {
//                if (ProgressServiceImpl.isFight(msg.getGroupCodeNumber()) != null) {
//                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), StartFightStartDouble);
//                    return;
//                }
//
//                String time = msg.getMsg().replaceAll(" +", "").substring(5).trim();
//                LocalDateTime startTime = getTodayFive(LocalDateTime.now());
//                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//                if (!time.equals("")) {
//                    LocalDate localDate = LocalDate.parse(time, df);
//                    startTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
//                }
//                Progress progress = new Progress();
//                progress.setLoop(1);
//                progress.setSerial(1);
//                progress.setRemnant(BossHpLimit[0]);
//                progress.setTeamQQ(msg.getGroupCodeNumber());
//                progress.setVersion(1);
//                progress.setStartTime(startTime);
//                progress.setEndTime(startTime.plusDays(6));
//
//                ProgressServiceImpl.save(progress);
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功记录会战\n" + startTime.format(df) + "到" + startTime.plusDays(8).format(df));
//            } else {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
//            }
//        } catch (NullPointerException e) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还未加入或建立公会");
//        } catch (Exception e) {
//            e.printStackTrace();
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "日期格式错误，示例 2020-05-04");
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#查树", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void searchTree(GroupMsg msg, MsgSender sender) {
//        List<Tree> trees = treeServiceImpl.getTreeByGroup(msg.getGroupCodeNumber());
//
//        if (trees.size() == 0) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "无 人 挂 树");
//        } else {
//            StringBuilder stringBuilder = new StringBuilder("挂树名单:");
//            for (Tree tree : trees) {
//                stringBuilder.append("\n[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
//            }
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#正在出刀", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void searchOutKnife(GroupMsg msg, MsgSender sender) {
//        List<Tree> trees = treeServiceImpl.getFightByGroup(msg.getGroupCodeNumber());
//
//        if (trees.size() == 0) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "无 人 出 刀");
//        } else {
//            StringBuilder stringBuilder = new StringBuilder("正在出刀:");
//            for (Tree tree : trees) {
//                stringBuilder.append("\n[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
//            }
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"boss状态", "boss", "boss咋样了", "boss还好吗"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
//    public void getBoss(GroupMsg msg, MsgSender sender) {
//        Progress progress = ProgressServiceImpl.getProgress(msg.getGroupCodeNumber());
//        if (progress != null) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "现在为boss为" + progress.getLoop() + "-" + progress.getSerial() + "\n剩余血量："
//                    + progress.getRemnant());
//        } else {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notBossOrNotDate);
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"#生成excel"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void getExcel(GroupMsg msg, MsgSender sender) {
//        String[] cmd = msg.getMsg().split(" +");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        ArrayList<LocalDate> arrayList = new ArrayList<>();
//        try {
//            if (cmd.length == 2) {
//                arrayList.add(LocalDate.parse(cmd[1], formatter));
//            } else if (cmd.length == 3) {
//                arrayList.addAll(getDescDateList(LocalDate.parse(cmd[1], formatter), LocalDate.parse(cmd[2], formatter)));
//            } else if (cmd.length == 1) {
//                arrayList.add(LocalDate.now());
//            }
//        } catch (DateTimeParseException e) {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "日期格式错误");
//            return;
//        }
//
//        //验证有没有公会
//        PcrUnion group = pcrUnionServiceImpl.getGroup(msg.getGroupCodeNumber());
//        if (group != null) {
//            try {
//                String groupQQ = msg.getGroupCode();//公会qq
//                ExcelWrite excelWrite;
//                if (arrayList.size() > 1) {
//                    excelWrite = new ExcelWrite(getExcelFileName(groupQQ, arrayList.get(0), arrayList.get(arrayList.size() - 1)), arrayList, msg.getGroupCodeNumber(),
//                            teamMemberServiceImpl,
//                            knifeListServiceImpl);
//                } else {
//                    excelWrite = new ExcelWrite(getExcelFileName(groupQQ, arrayList.get(0)), arrayList, msg.getGroupCodeNumber(),
//                            teamMemberServiceImpl,
//                            knifeListServiceImpl);
//                }
//
//                excelWrite.writedDate();
//                excelWrite.reflashFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), noThisGroup);
//        }
//    }
//
//    @Listen(MsgGetTypes.groupMemberReduce)
//    public void deleteTeam(GroupMemberReduce msg, MsgSender sender) {
//        String QQ = msg.getBeOperatedQQ();
//        long qq = Long.parseLong(QQ);
//        TeamMember teamMember = teamMemberServiceImpl.getTeamMemberByQQ(qq);
//        if (teamMember.getGroupQQ() == msg.getGroupCodeNumber()) {
//            teamMemberServiceImpl.removeById(teamMember);//删除记录
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), teamMember.getName() + " 离开了我们");
//        }
//    }
//
//    //代刀 @代刀的那个人 伤害值
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = "#代刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
//    public void sideKnife(GroupMsg msg, MsgSender sender) {
//        if (teamMemberServiceImpl.isAdmin(msg.getCodeNumber(), msg.getGroupCodeNumber())) {
//            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
//            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
//            int hurt = getHurt(msg.getMsg(), 2);
//            long qq = cqAtoNumber(strings.get(0));
//
//            KnifeState knifeState = toHurt(msg.getGroupCodeNumber(), qq, hurt, knifeListServiceImpl, ProgressServiceImpl, treeServiceImpl);
//            if (knifeState.isOk()) {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), knifeState.getMsg());
//            } else {
//                sender.SENDER.sendGroupMsg(msg.getGroupCode(), knifeState.getFailMsg());
//            }
//        } else {
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
//        }
//    }
}
