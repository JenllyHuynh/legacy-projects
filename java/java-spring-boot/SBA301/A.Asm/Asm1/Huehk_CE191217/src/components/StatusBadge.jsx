import { STATUS } from '../data/initialData'

export default function StatusBadge({ status }) {
  return (
    <span className={`badge ${status === 1 ? 'badge-on' : 'badge-off'}`}>{STATUS[status]}</span>
  )
}
