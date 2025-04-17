import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [videoFile, setVideoFile] = useState(null);
  const [videoUrl, setVideoUrl] = useState('');

  const fetchUserProfile = () => {
    axios.get('http://localhost:8080/api/auth/8/profile', { withCredentials: true })
      .then((response) => {
        if (response.data?.status === 200 && response.data.data) {
          const userData = response.data.data;
          setUser(userData);
          setVideoUrl(userData.videoUrl || '');
        }
      })
      .catch((error) => {
        console.error('Fetch error:', error);
      });
  };

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleUpload = () => {
    if (!selectedFile) {
      alert("Vui lòng chọn file Excel trước khi upload!");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);

    axios.post('http://localhost:8080/api/students/import', formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
      .then((response) => {
        alert("Import thành công: " + response.data.message);
      })
      .catch((error) => {
        console.error("Upload lỗi:", error);
        alert("Lỗi khi import file");
      });
  };

  const handleExport = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/students/export', {
        responseType: 'blob',
      });

      const contentType = response.headers['content-type'];
      if (contentType?.includes('application/json')) {
        const reader = new FileReader();
        reader.onload = () => {
          try {
            const json = JSON.parse(reader.result);
            alert(json.message || "Không thể export dữ liệu!");
          } catch (err) {
            alert("Không thể export dữ liệu (Lỗi nội dung không hợp lệ)");
          }
        };
        reader.readAsText(response.data);
        return;
      }

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'students.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Lỗi khi export file:", error);
      alert("Không thể export dữ liệu (Lỗi hệ thống)");
    }
  };

  const handleUploadVideo = () => {
    if (!videoFile) {
      alert("Vui lòng chọn video!");
      return;
    }

    const formData = new FormData();
    formData.append("video", videoFile);

    axios.post("http://localhost:8080/api/auth/video", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
      .then(() => {
        alert("Upload video thành công!");
        setVideoFile(null); // Xóa video tạm
        fetchUserProfile(); // Tải lại profile để cập nhật video mới
      })
      .catch((err) => {
        console.error("Lỗi khi upload video:", err);
        alert("Lỗi khi upload video");
      });
  };

  return (
    <div className="container">
      <h1>Thông tin người dùng</h1>

      {user ? (
        <div>
          <p><strong>Username:</strong> {user.username}</p>
          <p><strong>Name:</strong> {user.name}</p>

          {user.avatarUrl && (
            <div className="avatar-preview">
              <img src={user.avatarUrl} alt="Avatar" width="100" height="100" />
            </div>
          )}

          {videoUrl && (
            <div className="video-preview">
              <video width="480" controls>
                <source src={videoUrl} type="video/mp4" />
                Trình duyệt của bạn không hỗ trợ thẻ video.
              </video>
            </div>
          )}
        </div>
      ) : (
        <p>Đang tải thông tin...</p>
      )}

      <hr />

      <h2>Import danh sách sinh viên</h2>
      <input type="file" accept=".xlsx" onChange={handleFileChange} />
      <button onClick={handleUpload}>Upload file Excel</button>

      <hr />

      <h2>Export danh sách sinh viên</h2>
      <button onClick={handleExport}>Tải xuống file Excel</button>

      <hr />

      <h2>Upload video</h2>
      <input
        type="file"
        accept="video/*"
        onChange={(e) => {
          const file = e.target.files[0];
          if (file) {
            setVideoFile(file);
          }
        }}
      />
      <button onClick={handleUploadVideo}>Upload video</button>

      {videoFile && (
        <div className="video-preview">
          <p><em>Video preview trước khi upload:</em></p>
          <video width="480" controls>
            <source src={URL.createObjectURL(videoFile)} type={videoFile.type} />
            Trình duyệt của bạn không hỗ trợ thẻ video.
          </video>
        </div>
      )}
    </div>
  );
}

export default App;
