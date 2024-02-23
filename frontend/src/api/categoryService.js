import axios from 'axios';

export async function getCategoryList() {
  const res = await axios.get(`/api/categories`);
  return res.data;
}

export async function getCategoryById(categoryId) {
  const res = await axios.get(`/api/category/${categoryId}`);
  return res.data;
}
