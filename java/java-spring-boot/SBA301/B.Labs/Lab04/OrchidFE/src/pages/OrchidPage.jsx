import React, { useState, useEffect } from 'react';
import {
    getAllList,deleteOrchid

} from '../services/orchidService';
// thực hiện chức năng lấy danh sách.
export default function OrchidPage(){
    const [orchids,setOrchids]= useState([]);

    useEffect(() => {
        loadListOchids();
    }, []);

// method thực hiện in ra list danh sách
    const loadListOchids = async () => {
        try {
            const  dataFromList= await  getAllList();
            setOrchids(dataFromList);

        } catch (error){
            console.error("Error fetching orchids:", error);

        }
    };
    // method thực hiện xóa id của sản phẩm.
    const handleDelete = async (id) => {
        if (!window.confirm("Bạn có chắc chắn muốn xóa?")) return;
        try {
             await deleteOrchid(id);
            await loadListOchids();
            alert("Xóa thành công!");
        }catch (error){
            console.error("Error deleting orchid:", error);
        }
    }



    return (
        <div style={{ padding: '20px', maxWidth: '1000px', margin: '0 auto', fontFamily: 'Arial, sans-serif' }}>
            <h2>Danh Sách Hoa Lan (Orchid List)</h2>

            {/* BẢNG HIỂN THỊ DANH SÁCH (CHỈ ĐỌC - READ) */}
            <table border="1" cellPadding="10" style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr style={{ background: '#e2e2e2' }}>
                    <th>ID</th>
                    <th>Hình Ảnh</th>
                    <th>Tên Hoa Lan</th>
                    <th>Danh Mục</th>
                    <th>Mô Tả</th>
                    <th>Tự Nhiên</th>
                    <th>Cuốn Hút</th>
                    <th>Xóa hoa</th>
                </tr>
                </thead>
                <tbody>
                {orchids.map((item) => (
                    <tr key={item.id}>
                        <td>{item.id}</td>
                        <td>
                            {item.url && (
                                <img
                                    src={item.url}
                                    // alt={item}
                                    width="50"
                                    height="50"
                                    style={{ objectFit: 'cover' }}
                                />
                            )}
                        </td>
                        <td>{item.name}</td>
                        <td>{item.categoryId}</td>
                        <td>{item.description}</td>
                        <td>{item.isNatural ? 'Có' : 'Không'}</td>
                        <td>{item.isAttractive ? 'Có' : 'Không'}</td>
                        <td>
                            <button onClick={() => handleDelete(item.id)}>
                                Xóa
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>

    )


}