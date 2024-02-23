import axios from 'axios';

/**
 * GET /api/boards/param=
 *
 * @param searchCondition
 * @returns {Promise<any>}
 */
export async function getBoardList(searchCondition, pageSize) {
  const res = await axios.get(
    `/api/boards?startDate=${searchCondition.startDate}&endDate=${searchCondition.endDate}&searchText=${searchCondition.searchText}
    &categoryId=${searchCondition.categoryId}&pageNum=${searchCondition.pageNum}&pageSize=${pageSize}`,
  );
  return res.data;
}

/**
 * GET /api/board/boardId
 * @param boardId
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export async function getBoard(boardId, option) {
  const res = await axios.get(`/api/board/${boardId}?option=${option}`);
  if (res.data.errorCode) {
    throw new Error();
  }
  return res;
}

/**
 * POST /api/board
 * @param boardFormData
 * @returns {Promise<void>}
 */
export async function uploadBoard(boardFormData) {
  const formData = new FormData();
  // 파일 부분 설정
  boardFormData.files.forEach((file) => {
    formData.append('files', file);
  });
  // text 부분 설정
  formData.append('categoryId', boardFormData.selectedCategoryId);
  formData.append('userName', boardFormData.userName);
  formData.append('password', boardFormData.password);
  formData.append('passwordCheck', boardFormData.passwordCheck);
  formData.append('title', boardFormData.title);
  formData.append('content', boardFormData.content);

  await axios.post(`/api/board`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

/**
 * POST /api/board/check-password
 * @param boardId
 * @param password
 * @returns {Promise<any>}
 */
export async function checkPassword(boardId, password) {
  const res = await axios.post(`/api/board/check-password`, {
    boardId: boardId,
    password: password,
  });
  return res.data;
}

/**
 * DELETE /api/board/boardId
 * @param boardId
 * @returns {Promise<any>}
 */
export async function deleteBoard(boardId) {
  const res = await axios.delete(`/api/board/${boardId}`);
  return res.data;
}

/**
 * PUT /api/board/boardId
 * @param board
 * @returns {Promise<any>}
 */
export async function updateBoard(board) {
  const res = await axios.put(`/api/board/${board.boardId}`, {
    userName: board.userName,
    title: board.title,
    content: board.content,
  });
  return res.data;
}
