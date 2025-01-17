// src/api/classApi.js
import axios from 'axios';

export const fetchClasses = () => {
    return axios.get('/api/class'); // Giả sử backend có endpoint này
};
