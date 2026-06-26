# Login
1. Khi chạy app sẽ có UI để chọn role để login.
	1. Admin
	2. Student
	3. Exit
2. Nếu chọn Admin thì xem Quản lý sinh viên
3. Nếu chọn Student thì xem Đăng kí môn học cho sinh viên
# Quản lý thông tin sinh viên
1. Khi đăng nhập bằng role Admin thì có thể chọn xem list khóa học hoặc sinh viên, giảng viên.(3 mục)
2. Sau khi chọn 1 mục nào đó thì ngoài việc show list(danh sách giảng viên, danh sách khóa học, danh sách sinh viên) và ở bên dưới list đó sẽ có các option create, update, delete.
	1. Chỉ được thực hiện update/delete/create khi đó là khóa học(nghĩa là không có cập nhật cho sinh viên và giảng viên vì mặc định đã chốt dữ liệu rồi).
		1. Khi update thì phải chọn kì và sau đó 1 list class section được show ra.
		2. Chọn ID của CSession để thực hiện update.
			1. Tương tự cho delete
		3. Khi Create thì cứ bấm create rồi nhập thông tin thôi.
		4. Trong UI console này có Create Class, Update class, Delete class.
3. Ở UI của khóa học(sau khi chọn option khóa học thì phải nhập semester), sau khi nhập semester thì show list các course của kì đã nhập.
4. Ở dưới list có các option: quản lý khóa học như ở mục (2) và xem danh sách lớp.
5. Nếu chọn Xem danh sách lớp thì Mở ô nhập lớp muốn xem và xem được các student đã đăng kí ở lớp đó.
# Đăng kí môn học cho sinh viên
1. Khi đăng nhập bằng role Student thì có 3 option: Xem lịch học, Đăng kí môn học, Logout.
2. Khi chọn xem lịch học thì show list lịch học của sinh viên(xem ở Enrollment và Schedule). Có một option để quay lại UI trước nhé.
3. Khi chọn Đăng kí môn học thì vào UI mới: ở đây cần nhập mã môn học.
	1. Sau khi nhập mã môn học thì show list các Class Session của môn này(Các CSession ở đây vẫn còn đủ chứa).
	2. Sau đó chọn mã lớp để join vào.
	3. Thực hiện thành công thì in ra thông báo và quay lại trang trước đó.
4. Chọn logout thì out ra.
# Đọc dữ liệu từ tệp tin
1. Đọc danh sách student và cấp email, password.

# Mô tả 2
> Chủ đề xoay quanh Admin và Student.
# Admin
1. Sau khi đăng nhập:
2. Có 3 option:
	1. Xem danh sách sinh viên   (1)
	2. Xem danh sách giảng viên  (1)
	3. Xem danh sách khóa học (Bạn nào đó rảnh tay làm thêm khúc này để rẽ hướng cho nhập semester đi. Sau khi nhập semester thì làm các chức năng như các số đã đánh dấu)
		1. Nhập semester 
			1. Show list các môn có trong semester (2)
				1. Nhập môn
					1. Show list các lớp(Class Section) của môn học đó
					2. Chọn lớp
						1. Show ra giảng viên đảm nhiệm.
						2. Show list các sinh viên của lớp đó.
			2. Create ClassSection (3)
				1. Input CSection và các thông tin cần thiết.
			3. Update/Delete Class Section (4)
				1. Nhập môn học
					1. Show Class của môn học đó
					2. Chọn lớp bằng ID or mã lớp
					3. Thực hiện chỉnh sửa thông tin lớp(or delete)
						1. Thông tin lớp gồm có:
						2. Giảng viên đảm nhận.
						3. Lịch học.
						4. Tạm không có chức năng đổi lớp cho sinh viên.
			4. Tạo môn học trong kì (5)
				1. Nhập các thông tin cần thiết cho môn học(xem table Course)
			5. Chỉnh sửa môn học(bài Spring Boot nên làm, bài này thì next)
				1. Nhập môn học
					1. Show options: Update thông tin môn học(prequistite)

# Student
1. Sau khi đăng nhập:
	1. Xem lịch học (6)
		1. Show list các môn đã đăng kí và thời gian học.
		2. Chọn back để quay lại trang trước
	2. Đăng kí môn học (7)
		1. Nhập môn học
			1. Show ra các lớp đang có và vẫn còn đủ chứa(một lớp tối đa 30 sinh viên, có thể chỉnh sửa lại số nhỏ để dễ test).
				1. Nhập mã lớp muốn đăng kí(Phải check có trùng lịch với các môn đã có hay không).
				2. Sau khi đk thành công thì in ra thông báo và quay về phần (1. Nhập môn học).
		2. Back về trang trước
	3. Logout

# Login
1. Phân quyền cho Admin và Student (8)


