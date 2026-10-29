import { useState } from 'react'
import { Button, Form, InputGroup } from 'react-bootstrap'

export default function SearchBar({ placeholder = 'Search...', onSearch, children }) {
  const [value, setValue] = useState('')

  const submit = (e) => {
    e.preventDefault()
    onSearch(value.trim())
  }

  const clear = () => {
    setValue('')
    onSearch('')
  }

  return (
    <Form onSubmit={submit} className="d-flex flex-wrap gap-2">
      <InputGroup style={{ maxWidth: 360 }}>
        <Form.Control
          value={value}
          onChange={(e) => setValue(e.target.value)}
          placeholder={placeholder}
        />
        <Button type="submit" variant="outline-primary">Search</Button>
        <Button type="button" variant="outline-secondary" onClick={clear}>Clear</Button>
      </InputGroup>
      {children}
    </Form>
  )
}
