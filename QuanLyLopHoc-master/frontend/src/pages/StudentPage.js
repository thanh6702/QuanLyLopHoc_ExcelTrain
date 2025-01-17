// src/pages/StudentPage.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const StudentPage = () => {
    const [students, setStudents] = useState([]);

    // Hàm lấy danh sách học sinh từ API
    useEffect(() => {
        axios.get('/api/students')
            .then(response => {
                setStudents(response.data);
            })
            .catch(error => {
                console.error('Error fetching students:', error);
            });
    }, []);

    return (
        <div>
            <h2>Danh sách học sinh</h2>
            <ul>
                {students.map(student => (
                    <li key={student.id}>{student.name}</li>
                ))}
            </ul>
        </div>
    );
};

export default StudentPage;
