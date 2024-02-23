import axios from 'axios';

export async function downloadFile(fileId) {
  const res = await axios.get(`/api/file/${fileId}/download`, {
    responseType: 'blob',
  });

  return res.data;
}

export async function getFileList(boardId) {
  const res = await axios.get(`/api/files/${boardId}`);
  return res.data;
}

export async function deleteFile(fileId) {
  const res = await axios.delete(`/api/file/${fileId}`);
  return res.data;
}
