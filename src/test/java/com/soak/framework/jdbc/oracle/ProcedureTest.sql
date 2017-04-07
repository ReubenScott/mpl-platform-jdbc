CREATE OR REPLACE PROCEDURE p1(
       outputParam OUT int,
       id IN int,
       CURSOR1 out sys_refcursor,
       CURSOR2 out sys_refcursor
) AS
BEGIN
    select id into p1.outputParam from dual;
    OPEN CURSOR1 FOR select 'syj1' as id from dual union all select 'syj2' as id from dual;
    OPEN CURSOR2 FOR select 'abc1' as id from dual union all select 'abc2' as id from dual;
END p1;


CREATE OR REPLACE PROCEDURE p2(
       outputParam IN OUT int,
       id IN int,
       CURSOR1 out sys_refcursor
) AS
BEGIN
    select id into P2.outputParam from dual;
   OPEN CURSOR1 FOR select 'syj1' as id from dual union all select 'syj2' as id from dual;
END P2;

CREATE OR REPLACE PROCEDURE p3(
       outputParam IN OUT int,
       id IN int,
       CURSOR1 out sys_refcursor
) AS
BEGIN
    select id into P3.outputParam from dual;
   OPEN CURSOR1 FOR select 'syj1' as id from dual union all select 'syj2' as id from dual;
END P3;

CREATE OR REPLACE PROCEDURE p4
 AS
BEGIN
	 dbms_output.put_line('helloword');
END P4;
