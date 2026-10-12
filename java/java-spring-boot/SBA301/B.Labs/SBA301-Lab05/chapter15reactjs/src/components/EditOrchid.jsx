import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createOrchid, getOrchid, updateOrchid } from "../services/orchidApi";

const emptyForm = {
  orchidName: "",
  natural: false,
  orchidDescription: "",
  orchidCategory: "",
  attractive: false,
  orchidURL: "",
};

export default function EditOrchid({ createMode = false }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!createMode && id) {
      getOrchid(id)
        .then((r) => setForm(r.data))
        .catch((e) => setError(e.message));
    }
  }, [createMode, id]);

  const change = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((f) => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  };

  const submit = async (e) => {
    e.preventDefault();
    try {
      if (createMode) await createOrchid(form);
      else await updateOrchid(id, form);
      navigate("/");
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <form onSubmit={submit}>
      <h2>{createMode ? "Create Orchid" : "Edit Orchid"}</h2>
      {error && <div className="alert alert-danger">{error}</div>}

      <label className="form-label">Name</label>
      <input
        className="form-control mb-2"
        name="orchidName"
        value={form.orchidName ?? ""}
        onChange={change}
        required
      />

      <label className="form-label">Category</label>
      <input
        className="form-control mb-2"
        name="orchidCategory"
        value={form.orchidCategory ?? ""}
        onChange={change}
      />

      <label className="form-label">Description</label>
      <textarea
        className="form-control mb-2"
        name="orchidDescription"
        value={form.orchidDescription ?? ""}
        onChange={change}
      />

      <label className="form-label">Image URL</label>
      <input
        className="form-control mb-2"
        name="orchidURL"
        value={form.orchidURL ?? ""}
        onChange={change}
      />

      <div className="form-check mb-2">
        <input
          className="form-check-input"
          type="checkbox"
          name="natural"
          checked={!!form.natural}
          onChange={change}
        />
        <label className="form-check-label">Natural</label>
      </div>

      <div className="form-check mb-3">
        <input
          className="form-check-input"
          type="checkbox"
          name="attractive"
          checked={!!form.attractive}
          onChange={change}
        />
        <label className="form-check-label">Attractive</label>
      </div>

      <button className="btn btn-success" type="submit">Save</button>
    </form>
  );
}
