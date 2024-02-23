import axios from 'axios';

export async function getCommentList(boardId) {
  const res = await axios.get(`/api/comment/${boardId}`);
  return res.data;
}

export async function createComment(boardId, comment) {
  const res = await axios.post(`/api/comment`, {
    boardId: boardId,
    comment: comment,
  });
  return res.data;
}
