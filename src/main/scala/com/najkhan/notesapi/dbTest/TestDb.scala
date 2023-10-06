//package com.najkhan.notesapi.dbTest
//
//import cats.effect.unsafe.implicits.global
//import cats.free.Free
//import com.najkhan.notesapi.databaseUtil.DbUtil
//import doobie.free.connection
//import doobie.implicits._
//import org.slf4j.LoggerFactory
//
//class TestDb  {
//  private val logger = LoggerFactory.getLogger(classOf[TestDb])
//  private val createUserTable: Free[connection.ConnectionOp, Int] =
//    for {
//      a <-
//        sql"""
//          create table if not exists users (
//          id Bigint not null primary key auto_increment,
//          name varchar(250) not null,
//          username varchar(100) not null,
//          password longtext not null
//        );
//           """.update.run
//    } yield (a)
//  logger.warn(createUserTable.transact(DbUtil.transactor).unsafeRunSync().toString())
//
//  private val createNotesTable =
//    for {
//      a <-
//        sql"""
//          create table if not exists notes (
//          id Bigint not null primary key auto_increment,
//          title varchar(250),
//          notes longtext,
//          user_id bigint,
//          index title_idx(title),
//          index user_idx(user_id),
//          foreign key (user_id)
//          references users(id)
//        );
//           """.update.run
//    } yield (a)
//  logger.warn(createNotesTable.transact(DbUtil.transactor).unsafeRunSync().toString())
//
//  private val userTableData: Free[connection.ConnectionOp, Int] =
//    for {
//      a <-
//        sql"""
//          INSERT INTO users(id, name, username, password) VALUES
//          (1, 'naj','naj','someHash'),
//          (2, 'niki','nkki','someHash');
//           """.update.run
//    } yield (a)
//  logger.warn(userTableData.transact(DbUtil.transactor).unsafeRunSync().toString())
//
//
//  private val notesTableData: Free[connection.ConnectionOp, Int] =
//    for {
//      a <-
//        sql"""
//          INSERT INTO notes(title, notes, user_id) VALUES
//          ('first note','this is a very very large note', 1),
//          ('mot1 st note','this is a very very large note', 1),
//          ('2 nd note', 'this shold be second no  t',   1),
//          ('3 rd note','this is real third note', 1),
//          ('4 th note','this is 3 rd note', 1),
//          ('5 th note','this is a very large note one', 1),
//          ('6 th note',' fourth note is longer and cheerful 2', 1),
//          ('7 rd note','this is real third note 7', 1),
//          ('8 th note','this is 3 rd note 9', 2),
//          ('9 th note','this is a very large note 55', 2),
//          ('10 th note','fourth note is longer and cheerful 88', 2);
//           """.update.run
//    } yield (a)
//  logger.warn(notesTableData.transact(DbUtil.transactor).unsafeRunSync().toString())
//
//}
//
////object TestDb extends App {
////  new TestDb()
////}
