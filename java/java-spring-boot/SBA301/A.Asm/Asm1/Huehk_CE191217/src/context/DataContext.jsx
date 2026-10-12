import { createContext, useContext, useEffect, useState } from 'react'
import { loadData, saveData, removeData } from '../services/storage'
import { initialCategories, initialNews, initialUsers } from '../data/initialData'

// Dữ liệu dùng chung (categories/news/users) cho nhiều page -> đặt ở Context.
const DataContext = createContext(null)

export function useData() {
  return useContext(DataContext)
}

const KEYS = {
  categories: 'fun_categories',
  news: 'fun_news',
  users: 'fun_users',
}

const nextId = (list) => (list.length ? Math.max(...list.map((i) => i.id)) + 1 : 1)

// Tạo bộ thao tác CRUD bất biến (không mutate mảng gốc) cho một danh sách state
function makeCrud(setList) {
  return {
    add: (item) => setList((list) => [...list, { ...item, id: nextId(list) }]),
    update: (id, changes) =>
      setList((list) => list.map((i) => (i.id === id ? { ...i, ...changes } : i))),
    remove: (id) => setList((list) => list.filter((i) => i.id !== id)),
  }
}

export function DataProvider({ children }) {
  const [categories, setCategories] = useState(() => loadData(KEYS.categories, initialCategories))
  const [news, setNews] = useState(() => loadData(KEYS.news, initialNews))
  const [users, setUsers] = useState(() => loadData(KEYS.users, initialUsers))

  // Mỗi khi state đổi -> lưu xuống localStorage để reload không mất dữ liệu
  useEffect(() => saveData(KEYS.categories, categories), [categories])
  useEffect(() => saveData(KEYS.news, news), [news])
  useEffect(() => saveData(KEYS.users, users), [users])

  const resetData = () => {
    Object.values(KEYS).forEach(removeData)
    setCategories(initialCategories)
    setNews(initialNews)
    setUsers(initialUsers)
  }

  const value = {
    categories,
    news,
    users,
    categoryCrud: makeCrud(setCategories),
    newsCrud: makeCrud(setNews),
    userCrud: makeCrud(setUsers),
    resetData,
  }

  return <DataContext.Provider value={value}>{children}</DataContext.Provider>
}
