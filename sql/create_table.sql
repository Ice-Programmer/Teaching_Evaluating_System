-- 创建表
create database if not exists eva;

-- 使用表
use eva;

-- 管理员表
create table e_admin
(
    id          int auto_increment comment '用户名'
        primary key,
    username    varchar(256)      not null comment '账号',
    password    varchar(256)      not null comment '密码',
    role        tinyint default 1 not null comment '权限',
    addressIp   varchar(256)      null comment '最近一次登录的ip地址',
    addressName varchar(256)      null comment '形式:中国-省份-城市'
)
    comment '管理员表';

-- 教师表
create table e_teacher
(
    id       bigint auto_increment comment '主键'
        primary key,
    name     varchar(256) not null comment '教师名称',
    sex      tinyint      not null comment '性别',
    age      int          not null comment '年龄',
    position int          not null comment '职称',
    title    int          not null comment '职称',
    major    varchar(256) not null comment '专业（0-计算机，1-自动化）',
    email    varchar(512) not null comment '邮箱',
    identity tinyint      not null comment '国籍（0-俄罗斯，1-中国）'
)
    comment '教师表';


-- 学生表
create table e_student
(
    id          bigint auto_increment comment '主键'
        primary key,
    sid         varchar(256)  not null comment '学号',
    password    varchar(256)  null comment '密码',
    name        varchar(256)  not null comment '姓名',
    sex         tinyint       not null comment '性别(0-女 1-男）',
    age         int           not null comment '年龄',
    major       tinyint       not null comment '专业（0-计算机，1-自动化）',
    cid         int           not null comment '班级id',
    grade       int           not null comment '年级',
    tag         varchar(1024) null comment '用户名',
    addressIp   varchar(256)  null comment '最近登录的一次ip地址',
    addressName varchar(256)  null comment '形式:中国-省份-城市'
)
    comment '学生表';


-- 职位表
create table if not exists eva.`e_position`
(
    `id` int not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '职位名称'
) comment '职位表';

-- 职称表
create table if not exists eva.`e_title`
(
    `id` int not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '职称名称'
) comment '职称表';

-- 班级表
create table if not exists eva.`e_class`
(
    `id` int not null auto_increment comment '主键' primary key,
    `cid` varchar(256) not null comment '班级号'
) comment '班级表';

-- 课程表
create table if not exists eva.`e_course`
(
    `id` int not null auto_increment comment '主键' primary key,
    `cName` varchar(256) not null comment '课程中文名',
    `eName` varchar(256) not null comment '课程中文名',
    `major` varchar(256) not null comment '专业',
    `tid` int not null comment '教师id',
    `grade` int null comment '年级'
) comment '课程表';

-- 评测表
create table e_evaluate
(
    id          int auto_increment
        primary key,
    name        varchar(200) null comment '评测名称',
    create_time varchar(200) null comment '创建时间',
    start_time  varchar(200) null comment '开始时间',
    e_time      varchar(200) null comment '结束时间',
    status      int          null comment '发布状态（0-评测结束 1-正在评测）'
) comment '评测表';

-- 评价体系表
create table if not exists eva.`e_system`
(
    `id` int not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '指标名称',
    `level` int not null comment '评价级别',
    `kind` int not null comment '0为俄方，1为中方',
    `sid` int not null comment '二级指标指向一级指标'
) comment '评价体系表';

-- 一级指标表
create table e_mark_history
(
    id    int auto_increment
        primary key,
    tid   int           null comment '教师主键',
    cid   int           null comment '课程主键',
    eid   int           null comment '评价主键',
    score int           null comment '分数',
    sid   int           null comment '评价体系主键',
    aid   int           null comment '学生主键',
    state int default 0 null comment '0默认未完成，1为完成该一级测评'
) comment '一级指标表';


-- 总分表
create table e_score_history
(
    id    int auto_increment
        primary key,
    tid   int            null comment '教师主键',
    cid   int            null comment '课程主键',
    score decimal(10, 2) null comment '总分',
    eid   int            null comment '评价主键'
) comment '总分表';

-- 权重表
create table e_weight
(
    id     int auto_increment
        primary key,
    lid    int           null comment '一级指标的主键',
    weight decimal(2, 2) null comment '一级指标对应的权重'
) comment '权重表';

-- 红线表
create table e_redline_history
(
    id     int auto_increment
        primary key,
    tid         int            null comment '教师主键',
    gid         int            null comment '年级',
    cid         int            null comment '课程主键',
    score       decimal(10, 2) null comment '分数',
    happen_time varchar(200)   null comment '发生时间'
) comment '红线表';

-- 邮件发送记录
create table e_email_history
(
    id          int auto_increment
        primary key,
    name        varchar(200) null comment '操作人姓名',
    operation   varchar(200) null comment '操作',
    submit_time varchar(200) null comment '提交时间'
) comment '邮件记录表';

-- 红线指标
create table e_redline
(
    id    int auto_increment
        primary key,
    score decimal(10, 2) null comment '红线指标'
) comment '红线指标表';

-- 平均分表
create table if not exists eva.`e_average_score`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `tid` bigint not null comment '教师id',
    `sid` int not null comment '评价体系id',
    `score` int not null comment '分数'
) comment '平均分表';