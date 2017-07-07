create procedure p1
@outputParam int output,
@id          int
as
select @outputParam=@id
select  @outputParam+@outputParam
select  @outputParam

create procedure p2
@outputParam int output,
@id          int
as
select @outputParam=@id
return @outputParam 

create procedure p3
@outputParam int output,
@id          int
as
select @outputParam=@id
select @outputParam

create procedure p4
as
print 'helloword'

