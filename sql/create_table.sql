-- 创建表
create database if not exists eva;

-- 使用表
use eva;

-- 管理员表
create table e_admin
(
    id       int auto_increment comment '用户名'
        primary key,
    username varchar(256)      not null comment '账号',
    password varchar(256)      not null comment '密码',
    role     tinyint default 1 not null comment '权限'
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
    id       bigint auto_increment comment '主键'
        primary key,
    sid      varchar(256)  not null comment '学号',
    password varchar(256)  null comment '密码',
    name     varchar(256)  not null comment '姓名',
    sex      tinyint       not null comment '性别',
    age      int           not null comment '年龄',
    major    tinyint       not null comment '专业（0-计算机，1-自动化）',
    cid      int           not null comment '班级id',
    grade    int           not null comment '年级',
    tag      varchar(1024) null comment '用户名'
)
    comment '学生表';


