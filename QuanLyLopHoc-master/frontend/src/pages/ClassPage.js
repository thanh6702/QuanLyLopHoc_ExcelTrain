// src/pages/ClassPage.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const ClassPage = () => {
    const [classes, setClasses] = useState([]);

    // Hàm lấy danh sách lớp học từ API
    useEffect(() => {
        axios.get('/api/class')
            .then(response => {
                setClasses(response.data);
            })
            .catch(error => {
                console.error('Error fetching classes:', error);
            });
    }, []);

    return (
        <div>
            <h2>Danh sách lớp học</h2>
            <ul>
                {classes.map(classItem => (
                    <li key={classItem.id}>{classItem.name} - {classItem.code}</li>
                ))}
            </ul>
        </div>
    );
};

export default ClassPage;
