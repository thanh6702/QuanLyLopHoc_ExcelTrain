import React, { useState } from 'react';

export default function MediaUpload() {
  const [image, setImage] = useState(null);
  const [video, setVideo] = useState(null);
  const [excel, setExcel] = useState(null);

  const handleFileChange = (e, setter) => {
    const file = e.target.files[0];
    if (file) setter(file);
  };

  const downloadFile = (file, name) => {
    const url = URL.createObjectURL(file);
    const link = document.createElement('a');
    link.href = url;
    link.download = name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="p-6 space-y-6">
      {/* Upload & Display Image */}
      <div>
        <label className="block mb-2 font-semibold">Upload Image:</label>
        <input type="file" accept="image/*" onChange={(e) => handleFileChange(e, setImage)} />
        {image && (
          <div className="mt-4">
            <img src={URL.createObjectURL(image)} alt="Uploaded" className="w-48 rounded shadow" />
            <button onClick={() => downloadFile(image, 'downloaded-image.png')} className="mt-2 text-blue-500 underline">
              Download Image
            </button>
          </div>
        )}
      </div>

      {/* Upload & Display Video */}
      <div>
        <label className="block mb-2 font-semibold">Upload Video:</label>
        <input type="file" accept="video/*" onChange={(e) => handleFileChange(e, setVideo)} />
        {video && (
          <div className="mt-4">
            <video controls className="w-96 rounded shadow">
              <source src={URL.createObjectURL(video)} type={video.type} />
              Your browser does not support the video tag.
            </video>
            <button onClick={() => downloadFile(video, 'downloaded-video.mp4')} className="mt-2 text-blue-500 underline">
              Download Video
            </button>
          </div>
        )}
      </div>

      {/* Upload Excel File */}
      <div>
        <label className="block mb-2 font-semibold">Upload Excel File:</label>
        <input type="file" accept=".xlsx,.xls" onChange={(e) => handleFileChange(e, setExcel)} />
        {excel && (
          <div className="mt-4">
            <p className="mb-1">Uploaded: {excel.name}</p>
            <button onClick={() => downloadFile(excel, 'downloaded-file.xlsx')} className="text-blue-500 underline">
              Download Excel
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
