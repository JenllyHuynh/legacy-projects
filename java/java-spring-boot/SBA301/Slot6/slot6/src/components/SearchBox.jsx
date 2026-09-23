import Form from "react-bootstrap/Form";

export default function SearchBox({
                                      value,
                                      onChange,
                                      inputRef,
                                  }) {
    return (
        <Form.Control
            ref={inputRef}
            type="search"
            placeholder="Search products..."
            value={value}
            onChange={(event) => onChange(event.target.value)}
        />
    );
}